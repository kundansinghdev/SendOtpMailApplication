package com.SendOtpMailApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration // Marks this class as a configuration class
public class MailConfiguration {

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost); // Sets the SMTP server host
        mailSender.setPort(mailPort); // Sets the SMTP server port
        mailSender.setUsername(mailUsername); // Sets the SMTP server username
        mailSender.setPassword(mailPassword); // Sets the SMTP server password

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true"); // Enables STARTTLS for security
        return mailSender; // Returns the configured JavaMailSender
    }
}
