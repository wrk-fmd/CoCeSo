package at.wrk.coceso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "at.wrk")
public class CocesoCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CocesoCoreApplication.class, args);
    }
}
