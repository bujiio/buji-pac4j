package io.buji.pac4j.engine;

import io.buji.pac4j.profile.ShiroProfileManager;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.engine.DefaultLogoutLogic;

/**
 * Specialized LogoutLogic aimed for buji: makes a clean use of the ShiroProfileManager.
 *
 * @author Jerome LELEU
 * @since 5.0.0
 */
public class ShiroLogoutLogic<R, C extends WebContext> extends DefaultLogoutLogic<R, C> {

    public static final ShiroLogoutLogic INSTANCE = new ShiroLogoutLogic();

    public ShiroLogoutLogic() {
        super();
        this.setProfileManagerFactory(ShiroProfileManager::new);
    }
}
