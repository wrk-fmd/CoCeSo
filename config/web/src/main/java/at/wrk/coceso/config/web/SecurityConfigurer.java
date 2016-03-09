package at.wrk.coceso.config.web;

import at.wrk.coceso.auth.JsonAuthHandler;
import at.wrk.coceso.auth.JsonAuthError;
import at.wrk.coceso.auth.AuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfigurer {

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth, AuthenticationProvider authenticationProvider) {
    auth.authenticationProvider(authenticationProvider);
  }

  @Configuration
  @Order(1)
  public static class ClientSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired
    private JsonAuthError jsonAuthError;

    @Autowired
    private JsonAuthHandler jsonAuthHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.antMatcher("/client/**")
          .exceptionHandling().accessDeniedHandler(jsonAuthError).authenticationEntryPoint(jsonAuthError).and()
          .csrf().disable()
          .headers().disable()
          .logout().logoutUrl("/client/logout").and()
          .formLogin().loginProcessingUrl("/client/login").successHandler(jsonAuthHandler).failureHandler(jsonAuthHandler).and()
          .authorizeRequests()
          .antMatchers("/client/login").permitAll()
          .antMatchers("/client/**").authenticated();
    }
  }

  @Configuration
  @Order(2)
  public static class DataSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired
    private JsonAuthError jsonAuthError;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.antMatcher("/data/**")
          .exceptionHandling().accessDeniedHandler(jsonAuthError).authenticationEntryPoint(jsonAuthError).and()
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
    protected void configure(HttpSecurity http) throws Exception {
      http.antMatcher("/**")
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
    public void configure(WebSecurity web) throws Exception {
      web.ignoring().antMatchers("/static/**");
    }
  }

}
