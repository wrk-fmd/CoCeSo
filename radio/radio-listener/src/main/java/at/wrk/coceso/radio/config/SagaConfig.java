package at.wrk.coceso.radio.config;

import com.codebullets.sagalib.MessageStream;
import com.codebullets.sagalib.startup.EventStreamBuilder;
import com.codebullets.sagalib.startup.ReflectionsTypeScanner;
import org.reflections.Reflections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SagaConfig {

    private static final String SAGA_SCAN_PACKAGE = "at.wrk.coceso";

    @Bean
    public MessageStream messageStream(final SpringSagaProviderFactory sagaProviderFactory) {
        return EventStreamBuilder.configure()
                // Provide the Spring Provider Factory
                .usingSagaProviderFactory(sagaProviderFactory)
                // The default implementation does not work with a fat JAR
                .usingScanner(new ReflectionsTypeScanner(new Reflections(SAGA_SCAN_PACKAGE)))
                .build();
    }
}
