package io.buji.pac4j.engine;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.engine.DefaultSecurityLogic;

import io.buji.pac4j.profile.ShiroProfileManager;

public class ShiroSecurityLogic<R, C extends WebContext> extends DefaultSecurityLogic<R, C> {

    public ShiroSecurityLogic() {
        super();
        this.setProfileManagerFactory(ShiroProfileManager::new);
    }

}
