package io.buji.pac4j.context;

import org.apache.shiro.session.Session;

/**
 * Specific session store for Shiro, built from a given {@link Session}.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class ShiroProvidedSessionStore extends ShiroSessionStore {

    private final Session session;

    public ShiroProvidedSessionStore(final Session session) {
        this.session = session;
    }

    @Override
    protected Session getSession(final boolean createSession) {
        return session;
    }
}
