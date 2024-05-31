package cz.technico.inventory.authentication.email;

import cz.technico.inventory.user.UserDtoin;
import org.springframework.context.ApplicationEvent;

public class SignUpEvent extends ApplicationEvent {
    public SignUpEvent(UserDtoin userDtoin) {
        super(userDtoin);
    }
}
