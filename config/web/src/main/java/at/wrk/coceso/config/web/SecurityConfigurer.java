package at.wrk.coceso.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfigurer {

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth, final AuthenticationProvider authenticationProvider) {
        auth.authenticationProvider(authenticationProvider);
    }

    @Configuration
    @Order(1)
    public static class ClientSecurityConfigurer extends WebSecurityConfigurerAdapter {

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

    @Configuration
    @Order(2)
    public static class DataSecurityConfigurer extends WebSecurityConfigurerAdapter {

        private final AccessDeniedHandler accessDeniedHandler;
        private final AuthenticationEntryPoint authenticationEntryPoint;

        @Autowired
        public DataSecurityConfigurer(
                final AccessDeniedHandler accessDeniedHandler,
                final AuthenticationEntryPoint authenticationEntryPoint) {
            this.accessDeniedHandler = accessDeniedHandler;
            this.authenticationEntryPoint = authenticationEntryPoint;
        }

        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http.antMatcher("/data/**")
                    .exceptionHandling().accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(authenticationEntryPoint).and()
                    .csrf().disable()
                    .headers().disable()
                    .authorizeRequests()
                    .antMatchers("/data/socket").authenticated();
        }
    }

    @Configuration
    @Order(3)
    public static class ViewSecurityConfigurer extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http.antMatcher("/**")
                    .exceptionHandling().accessDeniedHandler(new LoggingAccessDeniedHandler()).and()
                    .csrf().disable()
                    .headers().disable()
                    .authorizeRequests()
                    .antMatchers("/logout").authenticated()
                    .and().logout().logoutSuccessUrl("/")
                    .and().formLogin().loginPage("/login").defaultSuccessUrl("/home", true).failureUrl("/login");
        }
    }

    @Configuration
    @Order(4)
    public static class NoneSecurityConfigurer extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(final WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/static/**");
        }
    }

}
