package at.wrk.coceso.radio.config;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import at.wrk.coceso.stomp.saga.UpdateReplaySaga;
import com.codebullets.sagalib.MessageStream;
import com.codebullets.sagalib.Saga;
import com.codebullets.sagalib.processing.SagaProviderFactory;
import com.codebullets.sagalib.startup.EventStreamBuilder;
import com.codebullets.sagalib.startup.ReflectionsTypeScanner;
import org.reflections.Reflections;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.inject.Provider;

@Configuration
public class SagaConfig {

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public UpdateReplaySaga updateReplaySaga() {
        return new UpdateReplaySaga();
    }

    @Bean
    public MessageStream messageStream(BeanFactory beanFactory) {
        SagaProviderFactory factory = new SagaProviderFactory() {
            @Override
            public <T extends Saga> Provider<T> createProvider(Class<T> sagaClass) {
                return () -> beanFactory.getBean(sagaClass);
            }
        };

        return EventStreamBuilder.configure()
                // Provide the Spring Provider Factory
                .usingSagaProviderFactory(factory)
                // The default implementation does not work with a fat JAR
                .usingScanner(new ReflectionsTypeScanner(new Reflections("at.wrk.coceso")))
                .build();
    }
}
