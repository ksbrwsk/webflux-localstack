package de.ksbrwsk.localstack;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AwsS3Router {

    @Bean
    RouterFunction<ServerResponse> http(AwsS3Handler s3Handler) {
        return nest(path("/api/s3"),
                route(GET(""), s3Handler::handleFetchAll)
                        .andRoute(POST("/upload"), s3Handler::handleUpload)
                        .andRoute(GET("/download/{name}"), s3Handler::downloadFromS3)
        );
    }
}
