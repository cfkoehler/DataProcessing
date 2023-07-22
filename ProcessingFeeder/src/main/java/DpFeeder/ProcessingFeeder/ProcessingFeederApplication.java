package DpFeeder.ProcessingFeeder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProcessingFeederApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessingFeederApplication.class, args);
	}

}
