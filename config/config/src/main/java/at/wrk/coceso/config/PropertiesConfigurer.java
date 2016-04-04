package at.wrk.coceso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:coceso.properties")
public class PropertiesConfigurer {

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    PropertySourcesPlaceholderConfigurer propertySources = new PropertySourcesPlaceholderConfigurer();
//    propertySources.setIgnoreUnresolvablePlaceholders(true);
    return propertySources;
  }

}
