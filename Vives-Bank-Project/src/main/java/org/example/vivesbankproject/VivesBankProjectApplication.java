package org.example.vivesbankproject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class VivesBankProjectApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(VivesBankProjectApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n\n🕹️ SERVER IS RUNNING 🕹️\n\n");
    }
}
