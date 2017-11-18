package ie.cit.teambravo.cardsec;

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication(scanBasePackageClasses = {
		CardsecApplication.class
})
public class CardsecApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardsecApplication.class, args);
	}
}
