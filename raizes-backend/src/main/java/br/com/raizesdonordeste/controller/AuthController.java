
package br.com.raizesdonordeste.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import br.com.raizesdonordeste.model.Funcionario;
import br.com.raizesdonordeste.repository.FuncionarioRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final FuncionarioRepository funcionarioRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder,
            FuncionarioRepository funcionarioRepository) {

        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.funcionarioRepository = funcionarioRepository;
    }

    @PostMapping("/login")
    public TokenResponse login(
            @Valid @RequestBody LoginRequest dados) {

        try {
            var autenticacao = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dados.usuario(), dados.senha()));

            Funcionario funcionario = funcionarioRepository
                    .findByUsuario(autenticacao.getName())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Usuário não encontrado."));

            Instant agora = Instant.now();

            var claims = JwtClaimsSet.builder()
                    .issuer("raizes-backend")
                    .subject(funcionario.getUsuario())
                    .issuedAt(agora)
                    .expiresAt(agora.plus(1, ChronoUnit.HOURS))
                    .claim("roles", List.of(funcionario.getPerfil().name()))
                    .claim("idFuncionario", funcionario.getIdFuncionario())
                    .claim("idUnidade",
                            funcionario.getUnidade() != null
                                    ? funcionario.getUnidade().getIdUnidade()
                                    : 0L)
                    .build();

            var cabecalho = JwsHeader
                    .with(MacAlgorithm.HS256)
                    .build();

            String token = jwtEncoder.encode(
                    JwtEncoderParameters.from(cabecalho, claims)).getTokenValue();

            return new TokenResponse(token, "Bearer", 3600);

        } catch (AuthenticationException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário ou senha inválidos.");
        }
    }

    public record LoginRequest(
            @NotBlank String usuario,
            @NotBlank String senha) {
    }

    public record TokenResponse(
            String token,
            String tipo,
            long expiraEmSegundos) {
    }
}
