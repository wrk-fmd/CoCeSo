package at.wrk.coceso.config.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@Order(1)
public class ClientSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    public ClientSecurityConfigurer(
            final AccessDeniedHandler accessDeniedHandler,
            final AuthenticationEntryPoint authenticationEntryPoint,
            final AuthenticationSuccessHandler authenticationSuccessHandler,
            final AuthenticationFailureHandler authenticationFailureHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.antMatcher("/client/**").exceptionHandling().accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(authenticationEntryPoint).and()
                .csrf().disable()
                .headers().disable()
                .logout().logoutUrl("/client/logout").and()
                .formLogin().loginProcessingUrl("/client/login").successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler)
                .and()
                .authorizeRequests().antMatchers("/client/login").permitAll()
                .antMatchers("/client/**").authenticated();
    }
}
