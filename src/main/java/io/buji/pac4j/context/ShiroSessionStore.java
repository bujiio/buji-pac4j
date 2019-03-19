/*
 * Licensed to the bujiio organization of the Shiro project under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.buji.pac4j.context;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.Session;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.session.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Specific session store for Shiro.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class ShiroSessionStore implements SessionStore<JEEContext> {

    private final static Logger logger = LoggerFactory.getLogger(ShiroSessionStore.class);

    public final static ShiroSessionStore INSTANCE = new ShiroSessionStore();

    /**
     * Get the Shiro session (do not create it if it does not exist).
     *
     * @param createSession create a session if requested
     * @return the Shiro session
     */
    protected Session getSession(final boolean createSession) {
        return SecurityUtils.getSubject().getSession(createSession);
    }

    @Override
    public String getOrCreateSessionId(final JEEContext context) {
        final Session session = getSession(true);
        if (session != null) {
            return session.getId().toString();
        }
        return null;
    }

    @Override
    public Optional<Object> get(final JEEContext context, final String key) {
        final Session session = getSession(false);
        if (session != null) {
            return Optional.ofNullable(session.getAttribute(key));
        }
        return Optional.empty();
    }

    @Override
    public void set(final JEEContext context, final String key, final Object value) {
        final Session session = getSession(true);
        if (session != null) {
            try {
                session.setAttribute(key, value);
            } catch (final UnavailableSecurityManagerException e) {
                logger.warn("Should happen just once at startup in some specific case of Shiro Spring configuration", e);
            }
        }
    }

    @Override
    public boolean destroySession(final JEEContext context) {
        getSession(true).stop();
        return true;
    }

    @Override
    public Optional getTrackableSession(final JEEContext context) {
        return Optional.empty();
    }

    @Override
    public Optional<SessionStore<JEEContext>> buildFromTrackableSession(final JEEContext context, final Object trackableSession) {
        return Optional.empty();
    }

    @Override
    public boolean renewSession(final JEEContext context) {
        return false;
    }
}
