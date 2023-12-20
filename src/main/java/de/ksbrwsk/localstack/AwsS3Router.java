package de.ksbrwsk.localstack;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Configuration class for AWS S3 Router.
 * This class is responsible for defining the routes for AWS S3 operations.
 */
@Configuration
public class AwsS3Router {

    /**
     * Defines the routes for AWS S3 operations.
     * @param s3Handler The handler for AWS S3 operations
     * @return RouterFunction instance
     */
    @Bean
    RouterFunction<ServerResponse> http(AwsS3Handler s3Handler) {
        return nest(path("/api/s3"),
                route(GET(""), s3Handler::handleFetchAll)
                        .andRoute(POST("/upload"), s3Handler::handleUpload)
                        .andRoute(GET("/download/{name}"), s3Handler::downloadFromS3)
        );
    }
}