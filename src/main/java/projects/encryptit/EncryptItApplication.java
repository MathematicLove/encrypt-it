package projects.encryptit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EncryptItApplication {
    public static void main(String[] args) {
        SpringApplication.run(EncryptItApplication.class, args);
    }
}