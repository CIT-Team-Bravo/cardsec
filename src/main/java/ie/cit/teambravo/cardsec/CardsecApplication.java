package ie.cit.teambravo.cardsec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

@EnableCaching
@SpringBootApplication(scanBasePackageClasses = {
        CardsecApplication.class
})
public class CardsecApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardsecApplication.class, args);
    }

}
