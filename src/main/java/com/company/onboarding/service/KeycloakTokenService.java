package com.company.onboarding.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
@Service
public class KeycloakTokenService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String TOKEN_URL = "http://localhost:8180/realms/jmix-realm/protocol/openid-connect/token";
    private static final String CLIENT_ID = "superset";
    private static final String CLIENT_SECRET = "a7N0pfE85z3xdHwiXH44DTExxvC7iZnB";

    private String accessToken;
    private Instant expiry;

    public synchronized String getAccessToken() {
        if (accessToken != null && expiry != null && Instant.now().isBefore(expiry)) {
            return accessToken;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=client_credentials&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);

        Map<String, Object> tokenData = response.getBody();
        this.accessToken = (String) tokenData.get("access_token");
        int expiresIn = (int) tokenData.get("expires_in");
        this.expiry = Instant.now().plusSeconds(expiresIn - 10);

        return accessToken;
    }
}
