package at.wrk.coceso.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.OperationNameGenerator;

@Component
@Primary
public class SwaggerOperationNameGenerator implements OperationNameGenerator {

    private final OperationNameGenerator delegate;

    @Autowired
    public SwaggerOperationNameGenerator(OperationNameGenerator delegate) {
        this.delegate = delegate;
    }

    @Override
    public String startingWith(String s) {
        s = s.replaceAll("Using(GET|POST|PUT|PATCH|DELETE)$", "");
        return delegate.startingWith(s);
    }
}
