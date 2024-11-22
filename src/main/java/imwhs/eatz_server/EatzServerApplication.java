package imwhs.eatz_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EatzServerApplication {

	// TODO: 엔티티 Soft Delete 처리
	public static void main(String[] args) {
		SpringApplication.run(EatzServerApplication.class, args);
	}

}
