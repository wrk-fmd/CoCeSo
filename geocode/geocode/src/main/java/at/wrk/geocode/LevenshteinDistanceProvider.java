package at.wrk.geocode;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LevenshteinDistanceProvider {
    @Bean
    public LevenshteinDistance getLevenshteinDistance() {
        return LevenshteinDistance.getDefaultInstance();
    }
}
