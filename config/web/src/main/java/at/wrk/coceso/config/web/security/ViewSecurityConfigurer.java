package at.wrk.coceso.config.web.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(3)
public class ViewSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.antMatcher("/**")
                .csrf().disable()
                .headers().disable()
                .authorizeRequests()
                .antMatchers("/logout").authenticated()
                .and().logout().logoutSuccessUrl("/")
                .and().formLogin().loginPage("/login").defaultSuccessUrl("/home", true).failureUrl("/login");
    }
}
