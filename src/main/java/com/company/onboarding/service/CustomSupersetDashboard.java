package com.company.onboarding.service;

import io.jmix.supersetflowui.kit.component.JmixSupersetDashboard;

import java.time.Instant;

public class CustomSupersetDashboard extends JmixSupersetDashboard {

    @Override
    protected void fetchGuestToken() {
        // Bỏ trống để không tự gọi API lại.
        // Token sẽ được quản lý ở SupersetTokenService.
    }

    public void applyGuestToken(String token) {
        setGuestTokenInternal(token);
    }
//    private String currentEmbeddedId;
//    private String currentGuestToken;
//    private Instant tokenExpireAt;
//
//    @Override
//    protected void fetchGuestToken() {
//        if (currentEmbeddedId == null) {
//            return;
//        }
//
//        // Nếu token còn hạn thì bỏ qua
//        if (currentGuestToken != null && tokenExpireAt != null && Instant.now().isBefore(tokenExpireAt)) {
//            return;
//        }
//
//        // Gọi API để lấy token mới
//        String guestToken = fetchGuestTokenFromServer(currentEmbeddedId);
//        long ttlSeconds = 3600; // TODO: thay bằng exp từ response Superset
//
//        currentGuestToken = guestToken;
//        tokenExpireAt = Instant.now().plusSeconds(ttlSeconds);
//
//        setGuestTokenInternal(guestToken);
//    }
//
//    @Override
//    public void setEmbeddedId(String embeddedId) {
//        if (embeddedId == null || embeddedId.isBlank()) {
//            throw new IllegalArgumentException("Embedded ID must not be null or empty");
//        }
//
//        // Nếu embeddedId thay đổi → reset cache
//        if (!embeddedId.equals(currentEmbeddedId)) {
//            currentEmbeddedId = embeddedId;
//            currentGuestToken = null;
//            tokenExpireAt = null;
//        }
//
//        super.setEmbeddedId(embeddedId); // sẽ tự trigger fetchGuestToken()
//    }
//
//    private String fetchGuestTokenFromServer(String embeddedId) {
//        // TODO: gọi API Superset /api/v1/security/guest_token/
//        return "dummy_token_" + embeddedId;
//    }
}
