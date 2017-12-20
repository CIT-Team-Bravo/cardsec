package ie.cit.teambravo.cardsec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Spring Boot Application entry point
 */
@EnableCaching
@SpringBootApplication(scanBasePackageClasses = {
		CardsecApplication.class
})
public class CardsecApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardsecApplication.class, args);
	}

}
