package at.wrk.coceso.config.web;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext context) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(WebMvcConfigurer.class);

        FilterRegistration.Dynamic encodingFilter = context.addFilter("encodingFilter", new CharacterEncodingFilter("UTF-8", true));
        encodingFilter.addMappingForUrlPatterns(null, true, "/*");
        encodingFilter.setAsyncSupported(true);

        FilterRegistration.Dynamic securityFilter = context.addFilter("securityFilter", new DelegatingFilterProxy("springSecurityFilterChain"));
        securityFilter.addMappingForUrlPatterns(null, false, "/*");
        securityFilter.setAsyncSupported(true);

        ServletRegistration.Dynamic registration = context.addServlet("dispatcher", new DispatcherServlet(rootContext));
        registration.setLoadOnStartup(1);
        registration.setAsyncSupported(true);
        registration.addMapping("/");
    }

}
