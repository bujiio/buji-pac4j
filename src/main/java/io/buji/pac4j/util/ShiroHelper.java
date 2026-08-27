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
package io.buji.pac4j.util;

import io.buji.pac4j.subject.Pac4jPrincipal;
import io.buji.pac4j.token.Pac4jToken;
import lombok.val;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.IsFullyAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.IsRememberedAuthorizer;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Helper for Shiro.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class ShiroHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroHelper.class);

    private final static Authorizer IS_REMEMBERED_AUTHORIZER = new IsRememberedAuthorizer();

    private final static Authorizer IS_FULLY_AUTHENTICATED_AUTHORIZER = new IsFullyAuthenticatedAuthorizer();

    /**
     * Populate the authenticated user profiles in the Shiro subject.
     *
     * @param profiles the linked hashmap of profiles
     */
    public static void populateSubject(final LinkedHashMap<String, UserProfile> profiles) {
        if (profiles != null && !profiles.isEmpty()) {
            val listProfiles = ProfileHelper.flatIntoAProfileList(profiles);
            val subject = SecurityUtils.getSubject();
            try {
                if (IS_FULLY_AUTHENTICATED_AUTHORIZER.isAuthorized(null, null, listProfiles)) {
                    login(subject, listProfiles, false, subject.isAuthenticated());
                } else if (IS_REMEMBERED_AUTHORIZER.isAuthorized(null, null, listProfiles)) {
                    login(subject, listProfiles, true, subject.isRemembered());
                }
            } catch (final HttpAction e) {
                throw new TechnicalException(e);
            }
        }
    }

    /**
     * Log in the subject, unless it is already logged in as the same user: in that case, the existing principal is
     * updated with the new profiles.
     *
     * @param subject the Shiro subject
     * @param profiles the profiles
     * @param rememberMe whether the user is only remembered
     * @param alreadyLoggedIn whether the subject is already logged in (at the same authentication level)
     */
    protected static void login(final Subject subject, final List<UserProfile> profiles, final boolean rememberMe,
                                final boolean alreadyLoggedIn) {
        if (alreadyLoggedIn && refreshProfiles(subject, profiles)) {
            LOGGER.debug("Same user already logged in: no new Subject.login");
            return;
        }
        subject.login(new Pac4jToken(profiles, rememberMe));
    }

    /**
     * Update the profiles of the existing {@link Pac4jPrincipal} if it relates to the same user.
     *
     * Since Shiro v2.2, a {@code Subject.login} renews the session identifier (protection against session fixation):
     * calling it again for an already authenticated user (when the profiles are re-saved after a refresh token
     * exchange for example) would uselessly change the session identifier on each renewal.
     *
     * @param subject the Shiro subject
     * @param profiles the new profiles
     * @return whether the existing principal has been updated
     */
    protected static boolean refreshProfiles(final Subject subject, final List<UserProfile> profiles) {
        val principals = subject.getPrincipals();
        if (principals == null) {
            return false;
        }
        val principal = principals.oneByType(Pac4jPrincipal.class);
        if (principal == null || !isSameUser(principal.getProfiles(), profiles)) {
            return false;
        }
        val oldProfiles = principal.getProfiles();
        val oldName = principal.getName();
        principal.setProfiles(profiles);
        // the name is also stored as the primary principal by the realm: it must not change
        if (!CommonHelper.areEquals(oldName, principal.getName())) {
            principal.setProfiles(oldProfiles);
            return false;
        }
        // re-save the principals to handle the session stores which do not keep the objects by reference
        val session = subject.getSession(false);
        if (session != null && session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) != null) {
            session.setAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY, principals);
        }
        return true;
    }

    /**
     * Whether both lists of profiles relate to the same user: same clients and same identifiers.
     *
     * @param oldProfiles the existing profiles
     * @param newProfiles the new profiles
     * @return whether it is the same user
     */
    protected static boolean isSameUser(final List<UserProfile> oldProfiles, final List<UserProfile> newProfiles) {
        if (oldProfiles == null || oldProfiles.size() != newProfiles.size()) {
            return false;
        }
        for (int i = 0; i < newProfiles.size(); i++) {
            val oldProfile = oldProfiles.get(i);
            val newProfile = newProfiles.get(i);
            if (oldProfile == null || newProfile == null
                || !CommonHelper.areEquals(oldProfile.getClientName(), newProfile.getClientName())
                || !CommonHelper.areEquals(oldProfile.getId(), newProfile.getId())) {
                return false;
            }
        }
        return true;
    }
}
