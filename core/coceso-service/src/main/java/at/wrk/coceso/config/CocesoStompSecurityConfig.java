package at.wrk.coceso.config;

import at.wrk.coceso.dto.CocesoExchangeNames;
import at.wrk.fmd.mls.stomp.config.StompSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.AbstractSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;

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

    @Override
    protected void customizeExpressionHandler(AbstractSecurityExpressionHandler<Message<Object>> handler) {
        handler.setPermissionEvaluator(evaluator);
    }

    @Override
    protected MessageSecurityMetadataSourceRegistry customizeInbound(MessageSecurityMetadataSourceRegistry registry) {
        return registry
                .simpSubscribeDestMatchers(buildDestination(CocesoExchangeNames.STOMP_CONCERNS, "*"))
                .access("hasPermission(null, T(at.wrk.coceso.auth.AccessLevel).CONCERN_READ)")
                .anyMessage().permitAll();
    }
}
