//package com.company.onboarding.service;
//
//import com.company.onboarding.entity.User;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jmix.core.DataManager;
//import io.jmix.core.security.SystemAuthenticator;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.util.UUID;
//
//@Service
//public class KeycloakService {
//
//    private static final String KEYCLOAK_BASE_URL = "http://keycloak.local:8180";
//    private static final String REALM = "jmix-realm";
//    private static final String CLIENT_ID = "jmix-app";
//    private static final String CLIENT_SECRET = "phup3e88w6UbIY90yTRkpK4xJljbAuS6";
//
//    private final DataManager dataManager;
//    private final ObjectMapper mapper = new ObjectMapper();
//    private final SystemAuthenticator authenticator;
//
//    public KeycloakService(DataManager dataManager, SystemAuthenticator authenticator) {
//        this.dataManager = dataManager;
//        this.authenticator = authenticator;
//    }
//
//    /**
//     * Đồng bộ user từ Jmix sang Keycloak (sau khi commit).
//     * Luôn chạy trong context SystemAuthenticator để tránh lỗi auth.
//     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void syncUser(UUID userId) {
//        authenticator.withSystem(() -> {
//            try {
//                User user = dataManager.load(User.class).id(userId).optional().orElse(null);
//                if (user != null) {
//                    createOrUpdateUser(user.getUsername(),
//                            user.getEmail(),
//                            user.getFirstName(),
//                            user.getLastName());
//                }
//            } catch (Exception e) {
//                throw new RuntimeException("❌ Sync user failed: " + e.getMessage(), e);
//            }
//            return null;
//        });
//    }
//
//    private String getAccessToken() throws IOException {
//        String url = KEYCLOAK_BASE_URL + "/realms/" + REALM + "/protocol/openid-connect/token";
//        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//        conn.setRequestMethod("POST");
//        conn.setDoOutput(true);
//        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//        String body = "client_id=" + CLIENT_ID +
//                "&client_secret=" + CLIENT_SECRET +
//                "&grant_type=client_credentials";
//
//        try (OutputStream os = conn.getOutputStream()) {
//            os.write(body.getBytes(StandardCharsets.UTF_8));
//        }
//
//        int status = conn.getResponseCode();
//        if (status != 200) {
//            String err = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
//            throw new RuntimeException("Failed to get token: " + status + " → " + err);
//        }
//
//        String response = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//        return mapper.readTree(response).get("access_token").asText();
//    }
//
//    private String getUserIdByEmail(String token, String email) throws IOException {
//        String url = KEYCLOAK_BASE_URL + "/admin/realms/" + REALM + "/users?email=" + email;
//        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//        conn.setRequestMethod("GET");
//        conn.setRequestProperty("Authorization", "Bearer " + token);
//
//        if (conn.getResponseCode() == 200) {
//            String json = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//            JsonNode arr = mapper.readTree(json);
//            if (arr.isArray() && arr.size() > 0) {
//                return arr.get(0).get("id").asText();
//            }
//        }
//        return null;
//    }
//
//    public void createOrUpdateUser(String username, String email, String firstName, String lastName) throws Exception {
//        String token = getAccessToken();
//        String userId = getUserIdByEmail(token, email);
//
//        if (userId != null) {
//            updateUserOnKeycloak(token, userId, username, email, firstName, lastName);
//        } else {
//            createUserOnKeycloak(token, username, email, firstName, lastName);
//        }
//    }
//
//    private void createUserOnKeycloak(String token, String username, String email,
//                                      String firstName, String lastName) throws IOException {
//        String url = KEYCLOAK_BASE_URL + "/admin/realms/" + REALM + "/users";
//        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//        conn.setRequestMethod("POST");
//        conn.setDoOutput(true);
//        conn.setRequestProperty("Authorization", "Bearer " + token);
//        conn.setRequestProperty("Content-Type", "application/json");
//
//        String json = """
//        {
//          "username": "%s",
//          "email": "%s",
//          "firstName": "%s",
//          "lastName": "%s",
//          "enabled": true
//        }
//        """.formatted(username, email, firstName, lastName);
//
//        try (OutputStream os = conn.getOutputStream()) {
//            os.write(json.getBytes(StandardCharsets.UTF_8));
//        }
//
//        if (conn.getResponseCode() == 201) {
//            System.out.println("✅ Created user: " + username);
//        } else {
//            System.err.println("❌ Failed to create user " + username + " → " +
//                    new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8));
//        }
//    }
//
//    private void updateUserOnKeycloak(String token, String userId, String username,
//                                      String email, String firstName, String lastName) throws IOException {
//        String url = KEYCLOAK_BASE_URL + "/admin/realms/" + REALM + "/users/" + userId;
//        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//        conn.setRequestMethod("PUT");
//        conn.setDoOutput(true);
//        conn.setRequestProperty("Authorization", "Bearer " + token);
//        conn.setRequestProperty("Content-Type", "application/json");
//
//        String json = """
//        {
//          "username": "%s",
//          "email": "%s",
//          "firstName": "%s",
//          "lastName": "%s",
//          "enabled": true
//        }
//        """.formatted(username, email, firstName, lastName);
//
//        try (OutputStream os = conn.getOutputStream()) {
//            os.write(json.getBytes(StandardCharsets.UTF_8));
//        }
//
//        if (conn.getResponseCode() == 204) {
//            System.out.println("🔄 Updated user: " + username);
//        } else {
//            System.err.println("❌ Failed to update user " + username + " → " +
//                    new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8));
//        }
//    }
//
//    public void deleteUser(String email) throws Exception {
//        String token = getAccessToken();
//        String userId = getUserIdByEmail(token, email);
//
//        if (userId != null) {
//            String url = KEYCLOAK_BASE_URL + "/admin/realms/" + REALM + "/users/" + userId;
//            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//            conn.setRequestMethod("DELETE");
//            conn.setRequestProperty("Authorization", "Bearer " + token);
//
//            if (conn.getResponseCode() == 204) {
//                System.out.println("🗑️ Deleted user in Keycloak: " + email);
//            } else {
//                System.err.println("❌ Failed to delete user " + email + " → " +
//                        new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8));
//            }
//        } else {
//            System.out.println("⚠️ User not found in Keycloak (skip delete): " + email);
//        }
//    }
//}
