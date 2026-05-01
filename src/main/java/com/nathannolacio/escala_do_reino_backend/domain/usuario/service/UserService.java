package com.nathannolacio.escala_do_reino_backend.domain.usuario.service;

import com.nathannolacio.escala_do_reino_backend.core.security.JwtService;
import com.nathannolacio.escala_do_reino_backend.core.util.MaskUtils;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.exception.IgrejaNotFoundException;
import com.nathannolacio.escala_do_reino_backend.domain.igreja.repository.IgrejaRepository;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.dto.AuthResponse;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.exception.UsuarioJaVinculadoException;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.exception.UsuarioNotFoundException;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.model.User;
import com.nathannolacio.escala_do_reino_backend.domain.usuario.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final IgrejaRepository igrejaRepository;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, IgrejaRepository igrejaRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.igrejaRepository = igrejaRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse vincularIgreja(Long igrejaId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Tentativa de vincular usuário {} à igreja ID {}", MaskUtils.maskEmail(email), igrejaId);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Falha ao vincular: Usuário {} não encontrado", MaskUtils.maskEmail(email));
                    return new UsuarioNotFoundException(email);
                });

        if (user.getIgrejaId() != null && user.getIgrejaId() != 0L) {
            logger.warn("Falha ao vincular: Usuário {} já está vinculado à igreja ID {}", MaskUtils.maskEmail(email), user.getIgrejaId());
            throw new UsuarioJaVinculadoException();
        }

        if (!igrejaRepository.existsById(igrejaId)) {
            logger.warn("Falha ao vincular: Igreja ID {} não existe", igrejaId);
            throw new IgrejaNotFoundException(igrejaId);
        }

        user.setIgrejaId(igrejaId);
        userRepository.save(user);
        logger.info("Usuário {} vinculado com sucesso à igreja ID {}", MaskUtils.maskEmail(email), igrejaId);

        String newToken = jwtService.generateToken(user.getEmail(), user.getIgrejaId());
        return new AuthResponse(newToken);
    }
}
