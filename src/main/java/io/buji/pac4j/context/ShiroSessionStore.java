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
import org.apache.shiro.subject.support.DisabledSessionException;
import org.pac4j.core.context.WebContext;
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
public class ShiroSessionStore implements SessionStore {

    private static Logger LOGGER = LoggerFactory.getLogger(ShiroSessionStore.class);

    public static final ShiroSessionStore INSTANCE = new ShiroSessionStore();

    /**
     * Get the Shiro session (do not create it if it does not exist).
     *
     * @param createSession create a session if requested
     * @return the Shiro session
     */
    protected Session getSession(final boolean createSession) {
        try {
            final Session session = SecurityUtils.getSubject().getSession(createSession);
            LOGGER.debug("createSession: {}, retrieved session: {}", createSession, session);
            return session;
        } catch (final DisabledSessionException e) {
            return null;
        }
    }

    @Override
    public Optional<String> getSessionId(final WebContext context, final boolean createSession) {
        final Session session = getSession(createSession);
        if (session != null) {
            final String sessionId = session.getId().toString();
            LOGGER.debug("Get sessionId: {}", sessionId);
            return Optional.of(sessionId);
        } else {
            LOGGER.debug("No sessionId");
            return Optional.empty();
        }
    }

    @Override
    public Optional<Object> get(final WebContext context, final String key) {
        final Session session = getSession(false);
        if (session != null) {
            final Object value = session.getAttribute(key);
            LOGGER.debug("Get value: {} for key: {}", value, key);
            return Optional.ofNullable(value);
        } else {
            LOGGER.debug("Can't get value for key: {}, no session available", key);
            return Optional.empty();
        }
    }

    @Override
    public void set(final WebContext context, final String key, final Object value) {
        final Session session = getSession(true);
        if (session != null) {
            try {
                if (value instanceof Exception) {
                    LOGGER.debug("Set key: {} for value: {}", key, value.toString());
                } else {
                    LOGGER.debug("Set key: {} for value: {}", key, value);
                }
                session.setAttribute(key, value);
            } catch (final UnavailableSecurityManagerException e) {
                LOGGER.warn("Should happen just once at startup in some specific case of Shiro Spring configuration", e);
            }
        }
    }

    @Override
    public boolean destroySession(final WebContext context) {
        getSession(true).stop();
        return true;
    }

    @Override
    public Optional getTrackableSession(final WebContext context) {
        return Optional.empty();
    }

    @Override
    public Optional<SessionStore> buildFromTrackableSession(final WebContext context, final Object trackableSession) {
        return Optional.empty();
    }

    @Override
    public boolean renewSession(final WebContext context) {
        return false;
    }
}
