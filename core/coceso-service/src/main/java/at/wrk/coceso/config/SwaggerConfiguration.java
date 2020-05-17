package at.wrk.coceso.config;

import at.wrk.coceso.entity.Concern;
import at.wrk.coceso.entity.Container;
import at.wrk.coceso.entity.Incident;
import at.wrk.coceso.entity.Patient;
import at.wrk.coceso.entity.StaffMember;
import at.wrk.coceso.entity.Unit;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.Duration;
import java.time.Instant;

/**
 * Defines the Swagger configuration.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket api(BuildProperties buildProperties) {
        return new Docket(DocumentationType.SWAGGER_2)
                .directModelSubstitute(Instant.class, Double.class)
                .alternateTypeRules(
                        AlternateTypeRules.newRule(Duration.class, Long.class),
                        AlternateTypeRules.newRule(Concern.class, Long.class),
                        AlternateTypeRules.newRule(Incident.class, Long.class),
                        AlternateTypeRules.newRule(Unit.class, Long.class),
                        AlternateTypeRules.newRule(Patient.class, Long.class),
                        AlternateTypeRules.newRule(Container.class, Long.class),
                        AlternateTypeRules.newRule(StaffMember.class, Long.class)
                )
                .select()
                .apis(RequestHandlerSelectors.basePackage("at.wrk.coceso"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiEndPointsInfo(buildProperties));
    }

    private ApiInfo apiEndPointsInfo(BuildProperties buildProperties) {
        return new ApiInfoBuilder()
                .title("CoCeSo Core Service")
                .description("API of the CoCeSo Core Service")
                .license("MIT")
                .licenseUrl("https://opensource.org/licenses/MIT")
                .version(buildProperties.getVersion())
                .build();
    }
}
