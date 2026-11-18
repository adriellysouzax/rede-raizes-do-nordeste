
package br.com.raizesdonordeste.config;

import br.com.raizesdonordeste.model.Funcionario;
import br.com.raizesdonordeste.model.PerfilFuncionario;
import br.com.raizesdonordeste.repository.FuncionarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdministradorInicial {

    @Bean
    public CommandLineRunner criarAdministrador(
            FuncionarioRepository repository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            String senha = System.getenv("ADMIN_PASSWORD");

            if (repository.existsByUsuario("admin")) {
                return;
            }

            if (senha == null || senha.isBlank()) {
                throw new IllegalStateException(
                        "Configure a variável ADMIN_PASSWORD.");
            }

            Funcionario admin = new Funcionario();
            admin.setNome("Administrador Geral");
            admin.setUsuario("admin");
            admin.setSenha(passwordEncoder.encode(senha));
            admin.setPerfil(PerfilFuncionario.ADMIN);
            admin.setAtivo(true);

            repository.save(admin);
        };
    }
}
