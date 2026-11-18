
package br.com.raizesdonordeste.controller;

import br.com.raizesdonordeste.model.Funcionario;
import br.com.raizesdonordeste.model.PerfilFuncionario;
import br.com.raizesdonordeste.repository.FuncionarioRepository;
import br.com.raizesdonordeste.repository.UnidadeRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioController(
            FuncionarioRepository funcionarioRepository,
            UnidadeRepository unidadeRepository,
            PasswordEncoder passwordEncoder) {

        this.funcionarioRepository = funcionarioRepository;
        this.unidadeRepository = unidadeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Somente ADMIN cadastra funcionários
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public FuncionarioResposta cadastrar(
            @Valid @RequestBody FuncionarioCadastro dados) {

        if (dados.perfil() == PerfilFuncionario.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é permitido cadastrar outro administrador.");
        }

        if (funcionarioRepository.existsByUsuario(dados.usuario())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este usuário já está cadastrado.");
        }

        var unidade = unidadeRepository.findById(dados.idUnidade())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Unidade não encontrada."));

        Funcionario funcionario = new Funcionario();

        funcionario.setNome(dados.nome());
        funcionario.setUsuario(dados.usuario());
        funcionario.setSenha(passwordEncoder.encode(dados.senha()));
        funcionario.setPerfil(dados.perfil());
        funcionario.setUnidade(unidade);
        funcionario.setAtivo(true);

        return converter(funcionarioRepository.save(funcionario));
    }

    // ADMIN vê todos; GERENTE vê somente sua unidade
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public List<FuncionarioResposta> listar(
            Authentication authentication) {

        List<Funcionario> funcionarios;

        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (admin) {
            funcionarios = funcionarioRepository.findAll();
        } else {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            Number idUnidade = jwt.getClaim("idUnidade");

            if (idUnidade == null || idUnidade.longValue() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Gerente sem unidade vinculada.");
            }

            funcionarios = funcionarioRepository
                    .findByUnidade_IdUnidade(idUnidade.longValue());
        }

        return funcionarios.stream()
                .map(this::converter)
                .toList();
    }

    private FuncionarioResposta converter(Funcionario funcionario) {
        return new FuncionarioResposta(
                funcionario.getIdFuncionario(),
                funcionario.getNome(),
                funcionario.getUsuario(),
                funcionario.getPerfil(),
                funcionario.getUnidade() == null
                        ? null
                        : funcionario.getUnidade().getIdUnidade(),
                funcionario.getAtivo()
        );
    }

    public record FuncionarioCadastro(
            @NotBlank String nome,
            @NotBlank String usuario,
            @NotBlank @Size(min = 8) String senha,
            @NotNull PerfilFuncionario perfil,
            @NotNull @Positive Long idUnidade
    ) {}

    public record FuncionarioResposta(
            Long idFuncionario,
            String nome,
            String usuario,
            PerfilFuncionario perfil,
            Long idUnidade,
            Boolean ativo
    ) {}
}
