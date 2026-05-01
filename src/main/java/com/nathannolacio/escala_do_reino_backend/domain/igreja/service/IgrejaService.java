package com.nathannolacio.escala_do_reino_backend.domain.igreja.service;

import com.nathannolacio.escala_do_reino_backend.domain.igreja.dto.IgrejaRequest;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.dto.IgrejaResponse;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.model.Endereco;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.model.Igreja;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.repository.IgrejaRepository;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.exception.UsuarioJaVinculadoException;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.exception.UsuarioNotFoundException;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.model.User;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IgrejaService {
    private static final Logger logger = LoggerFactory.getLogger(IgrejaService.class);

    private final IgrejaRepository repository;
    private final UserRepository userRepository;

    public IgrejaService(IgrejaRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional
    public IgrejaResponse criar(IgrejaRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Usuário {} solicitou criação da igreja: {}", email, request.nome());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException(email));

        // Valida se o usuário já possui vínculo (ID diferente de 0)
        if (user.getIgrejaId() != null && user.getIgrejaId() != 0L) {
            logger.warn("Falha na criação: Usuário {} já está vinculado à igreja ID {}", email, user.getIgrejaId());
            throw new UsuarioJaVinculadoException();
        }

        Endereco endereco = new Endereco(
                request.logradouro(),
                request.numero(),
                request.complemento(),
                request.bairro(),
                request.cidade(),
                request.estado(),
                request.cep()
        );

        Igreja igreja = new Igreja();
        igreja.setNome(request.nome());
        igreja.setEndereco(endereco);

        Igreja salva = repository.save(igreja);
        
        // Vincula o criador à nova igreja automaticamente
        user.setIgrejaId(salva.getId());
        userRepository.save(user);
        
        logger.info("Igreja criada e usuário {} vinculado com sucesso. ID Igreja: {}", email, salva.getId());

        return toResponse(salva);
    }

    public List<IgrejaResponse> listarTodas(String nome) {
        logger.info("Listando igrejas com filtro de nome: '{}'", nome != null ? nome : "TODAS");
        
        List<Igreja> igrejas = (nome != null && !nome.isBlank())
                ? repository.findByNomeContainingIgnoreCase(nome.trim())
                : repository.findAll();

        logger.info("Total de igrejas encontradas: {}", igrejas.size());

        return igrejas.stream()
                .map(this::toResponse)
                .toList();
    }

    private IgrejaResponse toResponse(Igreja igreja) {
        return new IgrejaResponse(
                igreja.getId(),
                igreja.getNome(),
                igreja.getEndereco() != null ? igreja.getEndereco().getCidade() : null,
                igreja.getEndereco() != null ? igreja.getEndereco().getEstado() : null
        );
    }
}
