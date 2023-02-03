package org.pac4j.framework.adapter;

import io.buji.pac4j.context.ShiroSessionStore;
import io.buji.pac4j.profile.ShiroProfileManager;
import org.pac4j.core.config.Config;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jee.adapter.JEEFrameworkAdapter;

/**
 * Shiro adapter.
 *
 * @author Jerome LELEU
 * @since 9.0.0
 */
public class FrameworkAdapterImpl extends JEEFrameworkAdapter {

    @Override
    public void applyDefaultSettingsIfUndefined(final Config config) {
        CommonHelper.assertNotNull("config", config);
        config.setProfileManagerFactoryIfUndefined((ctx, session) -> new ShiroProfileManager(ctx, session));
        config.setSessionStoreFactoryIfUndefined(p -> ShiroSessionStore.INSTANCE);

        super.applyDefaultSettingsIfUndefined(config);
    }

    @Override
    public String toString() {
        return "Shiro";
    }
}
