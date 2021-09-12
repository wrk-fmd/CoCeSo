package at.wrk.coceso.config;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Container;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.StaffMember;
import at.wrk.coceso.entity.Unit;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.util.PrimitiveType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.time.Duration;
import java.time.Instant;

@Configuration
class SpringdocConfiguration {

    @Bean
    public OpenAPI springDocOpenAPI(SpringDocConfigProperties springDocConfigProperties, BuildProperties buildProperties) {
        ModelResolver.enumsAsRef = true;

        springDocConfigProperties.setDefaultProducesMediaType(MediaType.APPLICATION_JSON_VALUE);
        springDocConfigProperties.setWriterWithOrderByKeys(true);

        SpringDocUtils.getConfig().replaceWithClass(
                org.springframework.data.domain.Pageable.class,
                org.springdoc.core.converters.models.Pageable.class
        );

        // Map java.time classes to primitives
        addCustomClass(Instant.class, PrimitiveType.DOUBLE);
        addCustomClass(Duration.class, PrimitiveType.LONG);

        // Map entity parameters from their ids
        addCustomClass(Concern.class, PrimitiveType.LONG);
        addCustomClass(Incident.class, PrimitiveType.LONG);
        addCustomClass(Unit.class, PrimitiveType.LONG);
        addCustomClass(Patient.class, PrimitiveType.LONG);
        addCustomClass(Container.class, PrimitiveType.LONG);
        addCustomClass(StaffMember.class, PrimitiveType.LONG);

        return new OpenAPI()
                .info(new Info()
                        .title("CoCeSo Core Service")
                        .description("API of the CoCeSo Core Service")
                        .license(new License().name("MIT").url(("https://opensource.org/licenses/MIT")))
                        .version(buildProperties.getVersion())
                )
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components().addSecuritySchemes("JWT", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                ))
                ;
    }

    private void addCustomClass(Class<?> clazz, PrimitiveType type) {
        PrimitiveType.customClasses().put(clazz.getName(), type);
    }
}
