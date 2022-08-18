package io.buji.pac4j.bridge;

import io.buji.pac4j.context.ShiroSessionStore;
import io.buji.pac4j.profile.ShiroProfileManager;
import org.pac4j.core.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bridge from pac4j to Shiro.
 *
 * @author Jerome LELEU
 * @since 8.0.0
 */
public class Pac4jShiroBridge {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pac4jShiroBridge.class);

    public void setConfig(final Config config) {
        LOGGER.info("Initializing pac4j to Shiro bridge...");

        config.setProfileManagerFactory("ShiroProfileManager", (ctx, session) -> new ShiroProfileManager(ctx, session));
        config.setSessionStore(ShiroSessionStore.INSTANCE);
    }
}
