package com.company.onboarding.view.superset;


import com.company.onboarding.service.SupersetDashboardHolder;
import com.company.onboarding.service.SupersetTokenService;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "Superset", layout = MainView.class)
@ViewController(id = "Superset")
@ViewDescriptor(path = "Superset.xml")
public class Superset extends StandardView {

    @ViewComponent
    private com.vaadin.flow.component.html.Div container;

    @Autowired
    private SupersetTokenService supersetTokenService;

    @Autowired
    private SupersetDashboardHolder dashboardHolder;

    @Subscribe
    public void onInit(InitEvent event) {
        String embedId = "2ad9c913-18fd-49b0-af3d-506f0dd01bac";
        String guestToken = supersetTokenService.getGuestToken(embedId);

        container.removeAll();
        container.add(dashboardHolder.getDashboard(embedId, guestToken));
    }
}
