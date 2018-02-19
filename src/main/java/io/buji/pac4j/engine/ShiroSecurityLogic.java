package io.buji.pac4j.engine;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.engine.DefaultSecurityLogic;

import io.buji.pac4j.profile.ShiroProfileManager;

/**
 * Specialized SecurityLogic aimed for buji : makes a clean use of the ShiroProfileManager.
 *
 * @see DefaultSecurityLogic
 *
 * @author Andre Doherty
 * @Since 3.2.0
 */
public class ShiroSecurityLogic<R, C extends WebContext> extends DefaultSecurityLogic<R, C> {

    public ShiroSecurityLogic() {
        super();
        this.setProfileManagerFactory(ShiroProfileManager::new);
    }

}
