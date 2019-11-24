package at.wrk.coceso.radio.config;

import com.codebullets.sagalib.Saga;
import com.codebullets.sagalib.processing.SagaProviderFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Provider;

@Component
public class SpringSagaProviderFactory implements SagaProviderFactory {

    private final BeanFactory beanFactory;

    @Autowired
    public SpringSagaProviderFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public <T extends Saga> Provider<T> createProvider(final Class<T> sagaClass) {
        return () -> beanFactory.getBean(sagaClass);
    }
}
