package br.com.raizesdonordeste.service;

import br.com.raizesdonordeste.model.AuditoriaEvento;
import br.com.raizesdonordeste.repository.AuditoriaEventoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    private final AuditoriaEventoRepository repository;

    public AuditoriaService(AuditoriaEventoRepository repository) {
        this.repository = repository;
    }

    public void registrar(String evento, String entidade, Long idRegistro,
                          String responsavelInformado, String detalhes) {
        Authentication autenticacao =
                SecurityContextHolder.getContext().getAuthentication();

        String responsavel = responsavelInformado;
        if (responsavel == null || responsavel.isBlank()) {
            responsavel = autenticacao != null && autenticacao.isAuthenticated()
                    && !"anonymousUser".equals(autenticacao.getPrincipal())
                    ? autenticacao.getName()
                    : "SISTEMA";
        }

        AuditoriaEvento registro = new AuditoriaEvento();
        registro.setEvento(evento);
        registro.setEntidade(entidade);
        registro.setIdRegistro(idRegistro);
        registro.setResponsavel(responsavel);
        registro.setDetalhes(detalhes);
        repository.save(registro);
    }
}
