package at.ac.tuwien.sepr.groupphase.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.ActiveProfiles;

public class DevelopmentApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BackendApplication.class);
        app.setAdditionalProfiles("generateData", "test");
        app.run(args);
    }
}
