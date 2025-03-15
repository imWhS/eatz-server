package imwhs.eatz_server;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling
@SpringBootApplication
@EnableJpaAuditing
public class EatzServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EatzServerApplication.class, args);
	}

	@PostConstruct
	public void setTime() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

}
