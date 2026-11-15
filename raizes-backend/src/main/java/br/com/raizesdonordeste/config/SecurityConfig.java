
package br.com.raizesdonordeste.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import br.com.raizesdonordeste.service.FuncionarioDetailsService;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // Permite consultar o cardápio sem autenticação
    @Bean
    @Order(1)
    public SecurityFilterChain cardapioSecurity(HttpSecurity http)
            throws Exception {

        http.securityMatcher("/produtos/cardapio")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/produtos/cardapio")
                        .permitAll()
                        .anyRequest().denyAll());

        return http.build();
    }

    // Configura autenticação JWT e permissões da API
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurity(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter)
            throws Exception {

        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Login público
                        .requestMatchers(HttpMethod.POST, "/auth/login")
                        .permitAll()

                        // Documentação Swagger/OpenAPI pública
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Administração das unidades
                        .requestMatchers("/unidades/**")
                        .hasRole("ADMIN")

                        // Controle de estoque
                        .requestMatchers("/estoque/**")
                        .hasAnyRole("ADMIN", "GERENTE")

                        // Demais rotas exigem autenticação
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(
                                jwtAuthenticationConverter)));

        return http.build();
    }

    // Autenticação dos funcionários
    @Bean
    public AuthenticationManager authenticationManager(
            FuncionarioDetailsService funcionarioDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(funcionarioDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    // Geração dos tokens JWT
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(
                new ImmutableSecret<>(obterChave()));
    }

    // Validação dos tokens JWT
    @Bean
    public JwtDecoder jwtDecoder() {
        var chave = new SecretKeySpec(
                obterChave(), "HmacSHA256");

        return NimbusJwtDecoder.withSecretKey(chave)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    // Chave secreta configurada por variável de ambiente
    private byte[] obterChave() {
        String segredo = System.getenv("JWT_SECRET");

        if (segredo == null ||
                segredo.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET precisa ter pelo menos 32 bytes.");
        }

        return segredo.getBytes(StandardCharsets.UTF_8);
    }
}
