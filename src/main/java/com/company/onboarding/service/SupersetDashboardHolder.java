package com.company.onboarding.service;

import io.jmix.supersetflowui.kit.component.JmixSupersetDashboard;
import org.springframework.stereotype.Component;

@Component   // 👈 đảm bảo có annotation này
public class SupersetDashboardHolder {

    private JmixSupersetDashboard dashboard;

    public JmixSupersetDashboard getDashboard(String embedId, String token) {
        if (dashboard == null) {
            dashboard = new JmixSupersetDashboard();
            dashboard.setEmbeddedId(embedId);
            dashboard.getElement().setProperty("guestToken", token);
        }
        return dashboard;
    }
}
