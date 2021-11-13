package de.ksbrwsk.localstack;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@Slf4j
@RequiredArgsConstructor
public class AwsS3Handler {
    private final AwsS3Service awsS3Service;

    Mono<ServerResponse> handleFetchAll(ServerRequest serverRequest) {
        log.info("handle request {} - {}", serverRequest.method(), serverRequest.requestPath());
        List<S3ObjectSummary> s3ObjectSummaries = this.awsS3Service.listObjects();
        return ok()
                .bodyValue(s3ObjectSummaries);
    }

    Mono<ServerResponse> downloadFromS3(ServerRequest serverRequest) {
        log.info("handle request {} - {}", serverRequest.method(), serverRequest.requestPath());
        String name = serverRequest.pathVariable("name");
        InputStreamResource inputStreamResource =
                new InputStreamResource(awsS3Service.downloadFileFromS3Bucket(name));
        return ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(BodyInserters.fromResource(inputStreamResource));
    }

    Mono<ServerResponse> handleUpload(ServerRequest serverRequest) {
        log.info("handle request {} - {}", serverRequest.method(), serverRequest.requestPath());
        return serverRequest
                .multipartData()
                .flatMap(
                        multiValueMap -> {
                            List<Part> file = multiValueMap.get("files");
                            Flux<String> uploadResult =
                                    Flux.fromIterable(file)
                                            .cast(FilePart.class)
                                            .flatMap(
                                                    filePart -> {
                                                        String fileName = filePart.filename();
                                                        return filePart
                                                                .content()
                                                                .filter(
                                                                        dataBuffer ->
                                                                                new byte[dataBuffer.readableByteCount()].length > 0)
                                                                .flatMap(
                                                                        dataBuffer -> {
                                                                            log.info("Upload file '{}' started", fileName);

                                                                            String etag = "";
                                                                            try {
                                                                                byte[] data = new byte[dataBuffer.readableByteCount()];
                                                                                dataBuffer.read(data);
                                                                                etag = awsS3Service.uploadObjectToS3(fileName, data);
                                                                                log.info("Upload file '{}' finished", fileName);
                                                                            } catch (Exception ex) {
                                                                                log.info("Upload file '{}' failed", fileName, ex);
                                                                            }

                                                                            return Mono.just(etag);

                                                                        });
                                                    });
                            return ok()
                                    .contentType(APPLICATION_JSON)
                                    .body(uploadResult, String.class);
                        });
    }
}
