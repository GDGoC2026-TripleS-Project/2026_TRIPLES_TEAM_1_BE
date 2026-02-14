package gdg.beforeonebite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BeforeOneBiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeforeOneBiteApplication.class, args);
	}

}
