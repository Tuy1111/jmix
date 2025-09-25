package com.company.onboarding.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
@Service
public class SupersetTokenService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final KeycloakTokenService keycloakTokenService;

    private String cachedToken;
    private Instant expiryTime;

    private static final String SUPERSET_URL = "http://localhost:8088/api/v1/security/guest_token/";
    private static final long TOKEN_TTL_SECONDS = 300;

    public SupersetTokenService(KeycloakTokenService keycloakTokenService) {
        this.keycloakTokenService = keycloakTokenService;
    }

    public synchronized String getGuestToken(String dashboardId) {
        if (cachedToken != null && expiryTime != null && Instant.now().isBefore(expiryTime)) {
            return cachedToken;
        }

        // Lấy access token từ Keycloak
        String accessToken = keycloakTokenService.getAccessToken();

        String payloadJson = """
            {
              "resources": [{"type":"dashboard","id":"%s"}],
              "rls": []
            }
        """.formatted(dashboardId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken); // 👈 Access Token từ Keycloak

        HttpEntity<String> request = new HttpEntity<>(payloadJson, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(SUPERSET_URL, request, Map.class);

        Map<String, Object> body = response.getBody();
        if (body != null && body.containsKey("token")) {
            cachedToken = (String) body.get("token");
            expiryTime = Instant.now().plusSeconds(TOKEN_TTL_SECONDS - 10);
            return cachedToken;
        } else {
            throw new RuntimeException("Không lấy được guest token từ Superset! Body=" + body);
        }
    }
}
