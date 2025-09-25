package com.company.onboarding.view.blank;


import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Value;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Route(value = "blank-view", layout = MainView.class)
@ViewController(id = "BlankView")
@ViewDescriptor(path = "blank-view.xml")
public class BlankView extends StandardView {
    @ViewComponent
    private IFrame supersetFrame;
    @Subscribe
    public void onInit(InitEvent event) {

        supersetFrame.setSrc("http://localhost:8088/superset/welcome/");
    }

//    @Subscribe
//    public void onInit(InitEvent event) {
//        String next = URLEncoder.encode("/superset/welcome/", StandardCharsets.UTF_8);
//        supersetFrame.setSrc("http://localhost:8088/login/keycloak?next=" + next + "&prompt=none");
//
//    }
}