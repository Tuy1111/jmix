package com.company.onboarding.config;

import com.company.onboarding.service.CustomSupersetDashboard;
import io.jmix.supersetflowui.kit.component.JmixSupersetDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SupersetUiConfig {

    @Bean("supersetDashboard")
    public JmixSupersetDashboard customSupersetDashboard() {
        return new CustomSupersetDashboard();
    }
}
