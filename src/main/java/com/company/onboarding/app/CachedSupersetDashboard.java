package com.company.onboarding.app;

import com.vaadin.flow.component.Tag;
import io.jmix.supersetflowui.kit.component.JmixSupersetDashboard;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Tag("cached-superset-dashboard")
public class CachedSupersetDashboard extends JmixSupersetDashboard {

    @Override
    protected void fetchGuestToken() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8088/cached_guest_token"))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            if (json.has("guest_token")) {
                String token = json.get("guest_token").getAsString();
                setGuestTokenInternal(token);
            } else {
                System.err.println("❌ Error fetching guest_token: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
