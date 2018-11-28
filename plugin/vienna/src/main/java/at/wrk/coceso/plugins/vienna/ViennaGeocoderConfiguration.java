package at.wrk.coceso.plugins.vienna;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ViennaGeocoderConfiguration {

    @Bean
    public ClientHttpRequestFactory createFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(500);
        factory.setConnectTimeout(1000);
        factory.setReadTimeout(2000);
        return factory;
    }

    @Bean
    public RestTemplate createRestTemplate(final ClientHttpRequestFactory clientHttpRequestFactory) {
        return new RestTemplate(clientHttpRequestFactory);
    }
}
