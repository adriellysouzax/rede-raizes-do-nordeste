
package br.com.raizesdonordeste.service;

import br.com.raizesdonordeste.model.Funcionario;
import br.com.raizesdonordeste.repository.FuncionarioRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class FuncionarioDetailsService implements UserDetailsService {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioDetailsService(
            FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usuario) {

        Funcionario funcionario = funcionarioRepository
                .findByUsuario(usuario)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado."));

        return User.withUsername(funcionario.getUsuario())
                .password(funcionario.getSenha())
                .roles(funcionario.getPerfil().name())
                .disabled(!Boolean.TRUE.equals(funcionario.getAtivo()))
                .build();
    }
}
