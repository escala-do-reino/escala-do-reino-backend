package com.nathannolacio.escala_do_reino_backend.core.config;

import com.nathannolacio.escala_do_reino_backend.core.security.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<Long> {

    @Override
    public Long resolveCurrentTenantIdentifier() {
        Long tenantId = TenantContext.getCurrentTenant();

        if (tenantId != null) {
            return tenantId;
        }

        // Se estamos dentro de uma requisição HTTP
        if (RequestContextHolder.getRequestAttributes() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            // Se o usuário está autenticado mas não temos o tenantId no contexto, 
            // lançamos um erro para evitar o fallback silencioso para o tenant 0.
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                throw new IllegalStateException("Falha de isolamento: Usuário autenticado sem ID de tenant no contexto.");
            }
        }

        // Fallback apenas para startup, processos em background ou requisições anônimas (Login/Register)
        return 0L;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
