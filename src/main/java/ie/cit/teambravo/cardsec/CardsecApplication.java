package ie.cit.teambravo.cardsec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
		CardsecApplication.class
})
public class CardsecApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardsecApplication.class, args);
	}
}
