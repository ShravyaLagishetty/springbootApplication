package com.example.bfh.service;

import com.example.bfh.model.GenerateWebhookRequest;
import com.example.bfh.model.GenerateWebhookResponse;
import com.example.bfh.model.SubmitRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class QualifierRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(QualifierRunner.class);

    private final WebClient webClient;
    private final SqlSolutionService sqlSolutionService;

    @Value("${app.candidate.name}")
    private String name;

    @Value("${app.candidate.email}")
    private String email;

    @Value("${app.candidate.regNo}")
    private String regNo;

    @Value("${app.endpoints.generateWebhook}")
    private String generateWebhookUrl;

    @Value("${app.endpoints.testWebhook}")
    private String testWebhookUrl;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting BFH qualifier flow…");
        log.info("Candidate: {} | {} | {}", name, email, regNo);

        // 1) Generate webhook
        GenerateWebhookRequest body = GenerateWebhookRequest.builder()
                .name(name)
                .regNo(regNo)
                .email(email)
                .build();

        GenerateWebhookResponse resp = webClient.post()
                .uri(generateWebhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(GenerateWebhookResponse.class)
                .doOnNext(r -> log.info("Webhook generated. webhook={}, accessToken=****", r.getWebhook()))
                .block();

        if (resp == null || resp.getAccessToken() == null) {
            throw new IllegalStateException("Failed to get accessToken from generateWebhook API");
        }

        // 2) Solve SQL based on regNo
        String finalSql = sqlSolutionService.resolveFinalSql(regNo);
        log.info("Resolved final SQL (length {} chars).", finalSql.length());

        // Optionally store the SQL locally (as the brief suggests storing result)
        Path out = Path.of("solution-final.sql");
        Files.writeString(out, finalSql);
        log.info("Stored final SQL at {}", out.toAbsolutePath());

        // 3) Submit the solution with JWT in Authorization header
        SubmitRequest submit = new SubmitRequest(finalSql);

        // Brief says use JWT in Authorization header; commonly "Bearer <token>"
        // If the endpoint expects raw token, change to set("Authorization", resp.getAccessToken()).
        String authValue = "Bearer " + resp.getAccessToken();

        String submitResponse = webClient.post()
                .uri(testWebhookUrl)
                .header(HttpHeaders.AUTHORIZATION, authValue)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(submit)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(ex -> {
                    log.error("Submission failed: {}", ex.getMessage(), ex);
                    return Mono.just("Submission failed: " + ex.getMessage());
                })
                .block();

        log.info("Submit API responded with: {}", submitResponse);
        log.info("BFH qualifier flow completed.");
    }
}
