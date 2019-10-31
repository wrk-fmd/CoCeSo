package at.wrk.coceso.gateway.replay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReplayProviderHandler {

    private final Map<String, ReplayProvider> providers;

    @Autowired
    public ReplayProviderHandler(List<ReplayProvider<?>> providers) {
        this.providers = providers.stream().collect(Collectors.toMap(ReplayProvider::getName, Function.identity()));
    }

    public ReplayProvider<?> getProvider(String name) {
        return providers.get(name);
    }
}
