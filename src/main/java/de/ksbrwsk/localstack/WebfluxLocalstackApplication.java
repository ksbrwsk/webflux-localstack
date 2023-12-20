package de.ksbrwsk.localstack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the WebfluxLocalstackApplication.
 * This class is responsible for starting the Spring Boot application.
 */
@SpringBootApplication
public class WebfluxLocalstackApplication {

    /**
     * The main method which serves as the entry point for the application.
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(WebfluxLocalstackApplication.class, args);
    }
}