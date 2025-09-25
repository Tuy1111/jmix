//package com.company.onboarding.listener;
//
//import com.company.onboarding.entity.User;
//import com.company.onboarding.service.KeycloakService;
//import io.jmix.core.event.EntityChangedEvent;
//import io.jmix.core.event.EntitySavingEvent;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.event.TransactionalEventListener;
//import org.springframework.transaction.event.TransactionPhase;
//
//import java.util.UUID;
//
//@Component
//public class UserEventListener {
//
//    private static final Logger log = LoggerFactory.getLogger(UserEventListener.class);
//    private final KeycloakService keycloakService;
//
//    public UserEventListener(KeycloakService keycloakService) {
//        this.keycloakService = keycloakService;
//    }
//
//    @EventListener
//    public void onUserSaving(final EntitySavingEvent<User> event) {
//        User user = event.getEntity();
//        if (user.getEmail() != null) {
//            user.setEmail(user.getEmail().toLowerCase());
//        }
//        log.debug("💾 Normalized user before save: {}", user.getEmail());
//    }
//
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void onUserChangedAfterCommit(final EntityChangedEvent<User> event) {
//        UUID userId = (UUID) event.getEntityId().getValue();
//        try {
//            switch (event.getType()) {
//                case CREATED, UPDATED -> {
//                    log.info("🔄 Syncing user [{}] to Keycloak...", userId);
//                    keycloakService.syncUser(userId);
//                }
//                case DELETED -> {
//                    String emailOld = event.getChanges().getOldValue("email");
//                    if (emailOld != null) {
//                        log.info("🗑️ Deleting user [{}] from Keycloak...", emailOld);
//                        keycloakService.deleteUser(emailOld);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("❌ Error syncing user [{}] with Keycloak: {}", userId, e.getMessage(), e);
//        }
//    }
//}
