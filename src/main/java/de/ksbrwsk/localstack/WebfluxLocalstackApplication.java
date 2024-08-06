package de.ksbrwsk.localstack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the WebfluxLocalstack application.
 * This class is responsible for bootstrapping the Spring Boot application.
 */
@SpringBootApplication
public class WebfluxLocalstackApplication {

    /**
     * The main method which serves as the entry point for the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(WebfluxLocalstackApplication.class, args);
    }
}