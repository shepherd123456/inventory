package cz.technico.inventory.authentication.email;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

public class BuyerRegistrationEvent extends ApplicationEvent {
    public BuyerRegistrationEvent(Map<String, String> emailPassword) {
        super(emailPassword);
    }
}
