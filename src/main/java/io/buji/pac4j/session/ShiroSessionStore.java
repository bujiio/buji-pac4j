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
package io.buji.pac4j.session;

import io.buji.pac4j.token.Pac4jToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.Session;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.IsFullyAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.IsRememberedAuthorizer;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Specific session store.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class ShiroSessionStore implements SessionStore<J2EContext> {

    private final static Authorizer<CommonProfile> IS_REMEMBERED_AUTHORIZER = new IsRememberedAuthorizer<>();

    private final static Authorizer<CommonProfile> IS_FULLY_AUTHENTICATED_AUTHORIZER = new IsFullyAuthenticatedAuthorizer<>();

    private final static Logger logger = LoggerFactory.getLogger(ShiroSessionStore.class);

    public final static ShiroSessionStore INSTANCE = new ShiroSessionStore();

    /**
     * Get the Shiro session (do not create it if it does not exist).
     *
     * @return the Shiro session
     */
    protected Session getSession() {
        return SecurityUtils.getSubject().getSession(false);
    }

    @Override
    public String getOrCreateSessionId(final J2EContext context) {
        final Session session = getSession();
        if (session != null) {
            return session.getId().toString();
        }
        return null;
    }

    @Override
    public Object get(final J2EContext context, final String key) {
        final Session session = getSession();
        if (session != null) {
            return session.getAttribute(key);
        }
        return null;
    }

    @Override
    public void set(final J2EContext context, final String key, final Object value) {
        if (Pac4jConstants.USER_PROFILES.equals(key)) {
            final LinkedHashMap<String, CommonProfile> profiles = (LinkedHashMap<String, CommonProfile>) value;
            final List<CommonProfile> listProfiles = ProfileHelper.flatIntoAProfileList(profiles);

            try {

                if (IS_FULLY_AUTHENTICATED_AUTHORIZER.isAuthorized(null, listProfiles)) {
                    SecurityUtils.getSubject().login(new Pac4jToken(profiles, false));
                } else if (IS_REMEMBERED_AUTHORIZER.isAuthorized(null, listProfiles)) {
                    SecurityUtils.getSubject().login(new Pac4jToken(profiles, true));
                }

            } catch (final HttpAction e) {
                throw new TechnicalException(e);
            }
        }

        final Session session = getSession();
        if (session != null) {
            try {
                session.setAttribute(key, value);
            } catch (final UnavailableSecurityManagerException e) {
                logger.warn("Should happen just once at startup in some specific case of Shiro Spring configuration", e);
            }
        }
    }
}
