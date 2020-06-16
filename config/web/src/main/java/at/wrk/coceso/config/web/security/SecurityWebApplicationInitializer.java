package at.wrk.coceso.config.web.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

@Configuration
@Order(1)
public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
}
