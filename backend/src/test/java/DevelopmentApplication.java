import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dataGenerator")
@SpringBootApplication
public class DevelopmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevelopmentApplication.class, args);
    }

}
