package at.wrk.coceso.config.web;

import at.wrk.coceso.config.CocesoConfig;
import at.wrk.coceso.utils.ActiveConcernResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.CacheControl;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@ComponentScan(basePackages = {"at.wrk.coceso"})
@EnableWebMvc
class WebMvcConfigurer extends WebMvcConfigurerAdapter {

  @Autowired
  private ActiveConcernResolver activeConcernResolver;

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    PageableHandlerMethodArgumentResolver pageable = new PageableHandlerMethodArgumentResolver();
    pageable.setOneIndexedParameters(true);
    argumentResolvers.add(pageable);
    argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    argumentResolvers.add(activeConcernResolver);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    localeChangeInterceptor.setParamName("lang");
    registry.addInterceptor(localeChangeInterceptor);

    WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
    webContentInterceptor.addCacheMapping(CacheControl.noCache(), "/data/**");
    registry.addInterceptor(webContentInterceptor);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/static/i18n/**").addResourceLocations("classpath:i18n/");
    registry.addResourceHandler("/static/**").addResourceLocations("/static/");
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
    stringConverter.setWriteAcceptCharset(false);
    converters.add(stringConverter);

    converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
    super.configureMessageConverters(converters);
  }

  @Bean
  public static InternalResourceViewResolver internalResourceViewResolver() {
    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
    resolver.setPrefix("/WEB-INF/pages/");
    resolver.setSuffix(".jsp");
    resolver.setExposedContextBeanNames("cocesoConfig");
    return resolver;
  }

  @Bean
  public static LocaleResolver localeResolver(CocesoConfig config) {
    CookieLocaleResolver localeResolver = new CookieLocaleResolver();
    localeResolver.setDefaultLocale(config.getDefaultLocale());
    localeResolver.setCookieName("lang");
    return localeResolver;
  }

  @Bean
  public static MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("classpath:i18n/messages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  @Bean
  public static LocalValidatorFactoryBean validator() {
    return new LocalValidatorFactoryBean();
  }

  @Bean
  public static ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    mapper.registerModule(new JavaTimeModule());
    return mapper;
  }

}
