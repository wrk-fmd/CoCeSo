package at.wrk.coceso.config;

import static at.wrk.coceso.auth.AccessLevel.CONCERN_READ;
import static at.wrk.coceso.auth.AccessLevel.CONTAINER_READ;
import static at.wrk.coceso.auth.AccessLevel.INCIDENT_READ;
import static at.wrk.coceso.auth.AccessLevel.MESSAGE_READ;
import static at.wrk.coceso.auth.AccessLevel.PATIENT_READ;
import static at.wrk.coceso.auth.AccessLevel.UNIT_READ;
import static at.wrk.coceso.dto.CocesoExchangeNames.STOMP_CONCERNS;
import static at.wrk.coceso.dto.CocesoExchangeNames.STOMP_CONTAINERS;
import static at.wrk.coceso.dto.CocesoExchangeNames.STOMP_INCIDENTS;
import static at.wrk.coceso.dto.CocesoExchangeNames.STOMP_MESSAGES;
import static at.wrk.coceso.dto.CocesoExchangeNames.STOMP_PATIENTS;
import static at.wrk.coceso.dto.CocesoExchangeNames.STOMP_UNITS;

import at.wrk.fmd.mls.stomp.config.StompSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.messaging.access.expression.DefaultMessageSecurityExpressionHandler;

/**
 * This class configures the security policy of the STOMP endpoint.
 */
@Configuration
class CocesoStompSecurityConfig extends StompSecurityConfig {

    private final PermissionEvaluator evaluator;

    @Autowired
    public CocesoStompSecurityConfig(PermissionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Bean
    public SecurityExpressionHandler<Message<Object>> messageSecurityExpressionHandler() {
        DefaultMessageSecurityExpressionHandler<Object> handler = new DefaultMessageSecurityExpressionHandler<>();
        handler.setPermissionEvaluator(evaluator);
        return handler;
    }

    @Override
    protected void customizeInbound(MessageSecurityMetadataSourceRegistry registry) {
        hasPermission(registry, STOMP_CONCERNS, CONCERN_READ);
        hasPermission(registry, STOMP_INCIDENTS, INCIDENT_READ);
        hasPermission(registry, STOMP_UNITS, UNIT_READ);
        hasPermission(registry, STOMP_CONTAINERS, CONTAINER_READ);
        hasPermission(registry, STOMP_PATIENTS, PATIENT_READ);
        hasPermission(registry, STOMP_MESSAGES, MESSAGE_READ);
    }
}
