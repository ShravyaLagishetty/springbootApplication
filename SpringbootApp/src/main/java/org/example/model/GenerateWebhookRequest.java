package com.example.bfh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GenerateWebhookRequest {
    private String name;
    private String regNo;
    private String email;
}
