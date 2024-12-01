package com.grs.grs_client;

import com.grs.core.dao.GrievanceDAO;
import com.grs.core.repo.grs.GrievanceRepo;
import com.grs.core.service.EmailService;
import com.grs.core.service.NotificationService;
import com.grs.core.service.OfficeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})

@SpringBootApplication
//@ComponentScan(basePackageClasses = {
//})
@EnableJpaRepositories(basePackages = {
        "com.grs.core.repo.grs",
        "com.grs.core.repo.projapoti"
})
@EntityScan(basePackages = "com.grs.core.domain")
@ComponentScan(basePackages = {
        "com.grs.core.service",
        "com.grs.core.dao"
})
public class GrsClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrsClientApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
