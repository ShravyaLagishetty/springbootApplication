package com.example.bfh.model;

import lombok.Data;

@Data
public class GenerateWebhookResponse {
    // Matches expected response keys from API (brief mentions 'webhook' & 'accessToken')
    private String webhook;
    private String accessToken;
}
