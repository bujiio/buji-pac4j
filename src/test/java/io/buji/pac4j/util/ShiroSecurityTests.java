package io.buji.pac4j.util;

import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.subject.Pac4jPrincipal;
import io.buji.pac4j.subject.Pac4jSubjectFactory;
import io.buji.pac4j.token.Pac4jToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.*;

/** Regression tests for authentication state, session persistence and mutable profiles. */
public final class ShiroSecurityTests {

    private Pac4jRealm realm;
    private DefaultSecurityManager securityManager;

    @Before
    public void setUp() {
        realm = new Pac4jRealm();
        realm.setCacheManager(new MemoryConstrainedCacheManager());
        securityManager = new DefaultSecurityManager(realm);
        securityManager.setSubjectFactory(new Pac4jSubjectFactory());
        ThreadContext.bind(securityManager);
    }

    @After
    public void tearDown() {
        ThreadContext.remove();
        securityManager.destroy();
    }

    private static CommonProfile profile(final boolean remembered, final boolean admin, final String token) {
        final CommonProfile profile = new CommonProfile();
        profile.setId("user");
        profile.setClientName("client");
        profile.setRemembered(remembered);
        profile.addAttribute("access_token", token);
        if (admin) {
            profile.addRole("admin");
            profile.addAttribute(Pac4jRealm.SHIRO_PERMISSIONS, List.of("document:delete"));
        }
        return profile;
    }

    private static LinkedHashMap<String, UserProfile> profiles(final UserProfile profile) {
        final LinkedHashMap<String, UserProfile> profiles = new LinkedHashMap<>();
        profiles.put("client", profile);
        return profiles;
    }

    private static Subject subject() {
        return SecurityUtils.getSubject();
    }

    private static Pac4jPrincipal principal() {
        return subject().getPrincipals().oneByType(Pac4jPrincipal.class);
    }

    private static Pac4jPrincipal sessionPrincipal() {
        final PrincipalCollection principals = (PrincipalCollection) subject().getSession()
            .getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        return principals.oneByType(Pac4jPrincipal.class);
    }

    private Subject nextRequest() {
        final Subject next = new Subject.Builder(securityManager).sessionId(subject().getSession().getId()).buildSubject();
        ThreadContext.bind(next);
        return next;
    }

    @Test
    public void testRememberMeStaysRememberedInTheCurrentAndNextRequests() {
        final Subject original = subject();
        ShiroHelper.populateSubject(profiles(profile(true, true, "at1")));
        assertSame(original, subject());
        assertFalse(original.isAuthenticated());
        assertTrue(original.isRemembered());
        assertFalse(nextRequest().isAuthenticated());
        assertTrue(subject().isRemembered());
    }

    @Test
    public void testRememberedProfileRenewalKeepsTheSessionId() {
        ShiroHelper.populateSubject(profiles(profile(true, false, "at1")));
        final Object sessionId = subject().getSession().getId();
        ShiroHelper.populateSubject(profiles(profile(true, false, "at2")));
        assertEquals(sessionId, subject().getSession().getId());
        assertFalse(subject().isAuthenticated());
        assertTrue(subject().isRemembered());
        assertEquals("at2", sessionPrincipal().getProfile().getAttribute("access_token"));
    }

    @Test
    public void testFullLoginAfterRememberMePersistsTheNewProfilesAndRights() {
        ShiroHelper.populateSubject(profiles(profile(true, true, "at1")));
        assertTrue(subject().hasRole("admin"));
        final Object sessionId = subject().getSession().getId();
        final String authenticationId = principal().getAuthenticationId();
        ShiroHelper.populateSubject(profiles(profile(false, false, "at2")));
        assertNotEquals(sessionId, subject().getSession().getId());
        assertNotEquals(authenticationId, principal().getAuthenticationId());
        assertEquals(principal().getAuthenticationId(), sessionPrincipal().getAuthenticationId());
        assertTrue(nextRequest().isAuthenticated());
        assertFalse(subject().hasRole("admin"));
        assertFalse(subject().isPermitted("document:delete"));
        assertEquals("at2", principal().getProfile().getAttribute("access_token"));
    }

    @Test
    public void testDowngradeToRememberMeIsPersisted() {
        ShiroHelper.populateSubject(profiles(profile(false, true, "at1")));
        ShiroHelper.populateSubject(profiles(profile(true, false, "at2")));
        assertFalse(subject().isAuthenticated());
        assertTrue(subject().isRemembered());
        assertFalse(nextRequest().isAuthenticated());
        assertTrue(subject().isRemembered());
        assertFalse(subject().hasRole("admin"));
        assertFalse(subject().isPermitted("document:delete"));
        assertEquals("at2", principal().getProfile().getAttribute("access_token"));
    }

    @Test
    public void testEquivalentProfilesFromANewLoginReplaceTheAuthenticationInSession() {
        ShiroHelper.populateSubject(profiles(profile(false, false, "at1")));
        final String authenticationId = principal().getAuthenticationId();
        subject().login(new Pac4jToken(List.of(profile(false, false, "at1")), false));
        assertNotEquals(authenticationId, principal().getAuthenticationId());
        assertEquals(principal().getAuthenticationId(), sessionPrincipal().getAuthenticationId());
    }

    @Test
    public void testAuthenticationCachingCannotReuseAPreviousLoginContext() {
        realm.setAuthenticationCachingEnabled(true);
        ShiroHelper.populateSubject(profiles(profile(false, false, "at1")));
        final String authenticationId = principal().getAuthenticationId();
        subject().login(new Pac4jToken(List.of(profile(false, false, "at1")), false));
        assertNotEquals(authenticationId, principal().getAuthenticationId());
        assertEquals(principal().getAuthenticationId(), sessionPrincipal().getAuthenticationId());
        assertEquals(0, realm.getAuthenticationCache().size());
    }

    @Test
    public void testInPlaceTokenRenewalDoesNotLeakTheAuthorizationCache() {
        final CommonProfile profile = profile(false, true, "at1");
        ShiroHelper.populateSubject(profiles(profile));
        assertTrue(subject().hasRole("admin"));
        final Object sessionId = subject().getSession().getId();
        final String authenticationId = principal().getAuthenticationId();
        for (int i = 0; i < 3; i++) {
            profile.addAttribute("access_token", "renewed-" + i);
            ShiroHelper.populateSubject(profiles(profile));
            assertTrue(subject().hasRole("admin"));
            assertEquals(1, realm.getAuthorizationCache().size());
        }
        assertEquals(sessionId, subject().getSession().getId());
        assertEquals(authenticationId, principal().getAuthenticationId());
        subject().logout();
        assertEquals(0, realm.getAuthorizationCache().size());
    }

    @Test
    public void testInPlaceRoleAndPermissionRevocationIsImmediate() {
        final CommonProfile profile = profile(false, true, "at1");
        ShiroHelper.populateSubject(profiles(profile));
        assertTrue(subject().hasRole("admin"));
        assertTrue(subject().isPermitted("document:delete"));
        profile.setRoles(Collections.emptySet());
        profile.removeAttribute(Pac4jRealm.SHIRO_PERMISSIONS);
        ShiroHelper.populateSubject(profiles(profile));
        assertFalse(subject().hasRole("admin"));
        assertFalse(subject().isPermitted("document:delete"));
        assertEquals(1, realm.getAuthorizationCache().size());
        nextRequest();
        assertFalse(subject().hasRole("admin"));
        assertFalse(subject().isPermitted("document:delete"));
    }

    @Test
    public void testInPlacePrincipalNameChangeRenewsTheSession() {
        realm.setPrincipalNameAttribute("email");
        final CommonProfile profile = profile(false, false, "at1");
        profile.addAttribute("email", "alice@example.com");
        ShiroHelper.populateSubject(profiles(profile));
        final Object sessionId = subject().getSession().getId();
        profile.addAttribute("email", "bob@example.com");
        ShiroHelper.populateSubject(profiles(profile));
        assertEquals("bob@example.com", subject().getPrincipal());
        assertNotEquals(sessionId, subject().getSession().getId());
        assertEquals("bob@example.com", nextRequest().getPrincipal());
    }

    @Test
    public void testInPlaceIdentityChangeRenewsTheSessionEvenIfTheNameIsUnchanged() {
        realm.setPrincipalNameAttribute("email");
        final CommonProfile profile = profile(false, true, "at1");
        profile.addAttribute("email", "same@example.com");
        ShiroHelper.populateSubject(profiles(profile));
        assertTrue(subject().hasRole("admin"));
        final Object sessionId = subject().getSession().getId();
        profile.setId("other-user");
        ShiroHelper.populateSubject(profiles(profile));
        assertNotEquals(sessionId, subject().getSession().getId());
        assertEquals("other-user", principal().getProfile().getId());
        assertEquals(0, realm.getAuthorizationCache().size());
    }

    @Test
    public void testANewSessionDoesNotReuseAnotherSessionsRights() {
        ShiroHelper.populateSubject(profiles(profile(false, true, "at1")));
        assertTrue(subject().hasRole("admin"));
        ThreadContext.unbindSubject();
        ShiroHelper.populateSubject(profiles(profile(false, false, "at2")));
        assertFalse(subject().hasRole("admin"));
    }

    @Test
    public void testEmptyAndAnonymousProfilesClearThePac4jSubject() {
        ShiroHelper.populateSubject(profiles(profile(false, true, "at1")));
        ShiroHelper.populateSubject(new LinkedHashMap<>());
        assertFalse(subject().isAuthenticated());
        assertNull(subject().getPrincipals());
        ShiroHelper.populateSubject(profiles(profile(false, true, "at1")));
        ShiroHelper.populateSubject(profiles(new AnonymousProfile()));
        assertFalse(subject().isAuthenticated());
        assertNull(subject().getPrincipals());
    }

    @Test
    public void testEmptyPac4jProfilesPreserveAnUnrelatedShiroLogin() {
        final SimpleAccountRealm accountRealm = new SimpleAccountRealm();
        accountRealm.addAccount("legacy", "password");
        securityManager.setRealms(List.of(accountRealm, realm));
        subject().login(new UsernamePasswordToken("legacy", "password"));
        ShiroHelper.populateSubject(new LinkedHashMap<>());
        assertTrue(subject().isAuthenticated());
        assertEquals("legacy", subject().getPrincipal());
    }
}
