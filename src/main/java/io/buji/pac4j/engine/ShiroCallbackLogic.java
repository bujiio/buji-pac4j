package io.buji.pac4j.engine;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.engine.DefaultCallbackLogic;

import io.buji.pac4j.profile.ShiroProfileManager;

/**
 * Specialized CallbackLogic aimed for buji : makes a clean use of the ShiroProfileManager.
 *
 * @see DefaultCallbackLogic
 *
 * @author Andre Doherty
 * @since 3.2.0
 */
public class ShiroCallbackLogic<R, C extends WebContext> extends DefaultCallbackLogic<R, C> {

    public ShiroCallbackLogic() {
        super();
        this.setProfileManagerFactory(ShiroProfileManager::new);
    }

}
