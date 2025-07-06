package ambient_intelligence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ambient_intelligence")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
