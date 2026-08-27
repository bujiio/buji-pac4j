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

import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.subject.Pac4jPrincipal;
import io.buji.pac4j.subject.Pac4jSubjectFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;

import java.util.LinkedHashMap;

import static org.junit.Assert.*;

/**
 * Tests the {@link ShiroHelper}.
 *
 * @since 9.1.2
 * @author Jerome Leleu
 */
public final class ShiroHelperTests {

    private static final String CLIENT_NAME = "clientName";

    private static final String CLIENT_NAME2 = "clientName2";

    private static final String ID = "id";

    private static final String ACCESS_TOKEN = "access_token";

    private static final String EMAIL = "email";

    /**
     * A realm which counts the authentications: a {@code Subject.login} triggers exactly one of them.
     */
    private static class CountingPac4jRealm extends Pac4jRealm {

        private int nbAuthentications = 0;

        @Override
        protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken token) throws AuthenticationException {
            nbAuthentications++;
            return super.doGetAuthenticationInfo(token);
        }
    }

    private CountingPac4jRealm realm;

    /**
     * Shiro renews the session identifier on login since v2.2 only (protection against session fixation):
     * the assertions on the session identifier renewal are skipped on the older versions.
     */
    private static boolean shiroRenewsSessionOnLogin() {
        try {
            DefaultSecurityManager.class.getDeclaredMethod("beforeSuccessfulLogin", Subject.class);
            return true;
        } catch (final NoSuchMethodException e) {
            return false;
        }
    }

    private static void assertSessionIdRenewed(final String previousSessionId) {
        Assume.assumeTrue(shiroRenewsSessionOnLogin());
        assertNotEquals(previousSessionId, sessionId());
    }

    @Before
    public void setUp() {
        realm = new CountingPac4jRealm();
        final DefaultSecurityManager securityManager = new DefaultSecurityManager(realm);
        securityManager.setSubjectFactory(new Pac4jSubjectFactory());
        ThreadContext.bind(securityManager);
    }

    @After
    public void tearDown() {
        ThreadContext.remove();
    }

    private static CommonProfile profile(final String clientName, final String id, final String accessToken) {
        final CommonProfile profile = new CommonProfile();
        profile.setId(id);
        profile.setClientName(clientName);
        profile.addAttribute(ACCESS_TOKEN, accessToken);
        return profile;
    }

    private static LinkedHashMap<String, UserProfile> profiles(final UserProfile... profiles) {
        final LinkedHashMap<String, UserProfile> map = new LinkedHashMap<>();
        for (final UserProfile profile : profiles) {
            map.put(profile.getClientName(), profile);
        }
        return map;
    }

    private static String sessionId() {
        return SecurityUtils.getSubject().getSession().getId().toString();
    }

    private static UserProfile shiroProfile() {
        return SecurityUtils.getSubject().getPrincipals().oneByType(Pac4jPrincipal.class).getProfile();
    }

    private static UserProfile sessionProfile() {
        final PrincipalCollection principals = (PrincipalCollection) SecurityUtils.getSubject().getSession()
            .getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        return principals.oneByType(Pac4jPrincipal.class).getProfile();
    }

    @Test
    public void testLoginRenewsTheSessionId() {
        final String sessionIdBefore = sessionId();

        ShiroHelper.populateSubject(profiles(profile(CLIENT_NAME, ID, "at1")));

        assertTrue(SecurityUtils.getSubject().isAuthenticated());
        assertEquals(1, realm.nbAuthentications);
        assertSessionIdRenewed(sessionIdBefore);
    }

    @Test
    public void testProfileRenewalKeepsTheSessionId() {
        ShiroHelper.populateSubject(profiles(profile(CLIENT_NAME, ID, "at1")));
        final String sessionIdAfterLogin = sessionId();

        ShiroHelper.populateSubject(profiles(profile(CLIENT_NAME, ID, "at2")));

        assertEquals(sessionIdAfterLogin, sessionId());
        assertTrue(SecurityUtils.getSubject().isAuthenticated());
        // no new Subject.login: the realm has not been called again
        assertEquals(1, realm.nbAuthentications);
        // but the profiles are up-to-date, both in the subject and in the session
        assertEquals("at2", shiroProfile().getAttribute(ACCESS_TOKEN));
        assertEquals("at2", sessionProfile().getAttribute(ACCESS_TOKEN));
    }

    @Test
    public void testProfileRenewalKeepsTheSessionIdForMultiProfiles() {
        ShiroHelper.populateSubject(profiles(profile(CLIENT_NAME, ID, "at1"), profile(CLIENT_NAME2, ID, "at1")));
        final String sessionIdAfterLogin = sessionId();

        ShiroHelper.populateSubject(profiles(profile(CLIENT_NAME, ID, "at2"), profile(CLIENT_NAME2, ID, "at1")));

        assertEquals(sessionIdAfterLogin, sessionId());
        assertEquals(1, realm.nbAuthentications);
        assertEquals(2, SecurityUtils.getSubject().getPrincipals().oneByType(Pac4jPrincipal.class).getProfiles().size());
        assertEquals("at2", shiroProfile().getAttribute(ACCESS_TOKEN));
    }

    @Test
    public void testNewIdentityRenewsTheSessionId() {
        ShiroHelper.populateSubject(profiles(profile(CLIENT_NAME, ID, "at1")));
        final String sessionIdAfterLogin = sessionId();

        ShiroHelper.populateSubject(profiles(profile(CLIENT_NAME, "id2", "at1")));

        assertEquals(2, realm.nbAuthentications);
        assertEquals("id2", shiroProfile().getId());
        assertSessionIdRenewed(sessionIdAfterLogin);
    }

    @Test
    public void testNewPrincipalNameRenewsTheSessionId() {
        realm.setPrincipalNameAttribute(EMAIL);
        final CommonProfile profile = profile(CLIENT_NAME, ID, "at1");
        profile.addAttribute(EMAIL, "john@example.com");
        ShiroHelper.populateSubject(profiles(profile));
        final String sessionIdAfterLogin = sessionId();
        assertEquals("john@example.com", SecurityUtils.getSubject().getPrincipal());

        final CommonProfile renewedProfile = profile(CLIENT_NAME, ID, "at2");
        renewedProfile.addAttribute(EMAIL, "jane@example.com");
        ShiroHelper.populateSubject(profiles(renewedProfile));

        assertEquals(2, realm.nbAuthentications);
        assertEquals("jane@example.com", SecurityUtils.getSubject().getPrincipal());
        assertSessionIdRenewed(sessionIdAfterLogin);
    }

    @Test
    public void testNewClientRenewsTheSessionId() {
        ShiroHelper.populateSubject(profiles(profile(CLIENT_NAME, ID, "at1")));
        final String sessionIdAfterLogin = sessionId();

        ShiroHelper.populateSubject(profiles(profile(CLIENT_NAME2, ID, "at1")));

        assertEquals(2, realm.nbAuthentications);
        assertSessionIdRenewed(sessionIdAfterLogin);
    }
}
