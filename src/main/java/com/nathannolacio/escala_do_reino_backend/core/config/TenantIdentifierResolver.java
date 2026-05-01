package com.nathannolacio.escala_do_reino_backend.core.config;

import com.nathannolacio.escala_do_reino_backend.core.security.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<Long> {

    @Override
    public Long resolveCurrentTenantIdentifier() {
        Long tenantId = TenantContext.getCurrentTenant();
        // Retornamos 0L (Igreja do Sistema) como fallback.
        // Isso evita que o Hibernate trave por falta de tenant e permite
        // que o banco valide a chave estrangeira (já que a igreja 0 existe).
        return tenantId != null ? tenantId : 0L;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
