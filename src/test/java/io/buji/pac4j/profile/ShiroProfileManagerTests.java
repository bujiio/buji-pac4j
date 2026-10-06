package io.buji.pac4j.profile;

import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.context.ShiroSessionStore;
import io.buji.pac4j.subject.Pac4jSubjectFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.context.WebContext;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;

import static org.junit.Assert.*;

/**
 * Tests the {@link ShiroProfileManager}.
 *
 * @author Jerome Leleu
 * @since 10.0.1
 */
public final class ShiroProfileManagerTests {

    public static final class ExpiringProfile extends CommonProfile {
        private static final long serialVersionUID = 1L;

        public ExpiringProfile() { }

        @Override
        public boolean isExpired() {
            return Boolean.TRUE.equals(getAttribute("expired"));
        }
    }

    @After
    public void tearDown() {
        ThreadContext.remove();
    }

    private static CommonProfile profile(final String id) {
        final CommonProfile profile = new CommonProfile();
        profile.setId(id);
        profile.setClientName("client");
        return profile;
    }

    private static void bind(final DefaultSecurityManager securityManager) {
        securityManager.setSubjectFactory(new Pac4jSubjectFactory());
        ThreadContext.bind(securityManager);
    }

    @Test
    public void testSave() {
        bind(new DefaultSecurityManager(new Pac4jRealm()));
        final ShiroProfileManager manager = new ShiroProfileManager(MockWebContext.create(), new MockSessionStore());

        manager.save(true, profile("id"), false);

        assertTrue(SecurityUtils.getSubject().isAuthenticated());
        assertTrue(manager.getProfile().isPresent());
    }

    @Test
    public void testProfilesRemovedWhenTheShiroLoginFails() {
        bind(new DefaultSecurityManager(new Pac4jRealm()) {
            @Override
            public Subject login(final Subject subject, final AuthenticationToken token) {
                // not an AuthenticationException
                throw new IllegalStateException("login failure");
            }
        });
        final ShiroProfileManager manager = new ShiroProfileManager(MockWebContext.create(), new MockSessionStore());

        assertThrows(IllegalStateException.class, () -> manager.save(true, profile("id"), false));

        assertFalse(SecurityUtils.getSubject().isAuthenticated());
        assertTrue(manager.getProfiles().isEmpty());
    }

    @Test
    public void testPreviousUserLoggedOutWhenTheShiroLoginFails() {
        final boolean[] fail = { false };
        bind(new DefaultSecurityManager(new Pac4jRealm()) {
            @Override
            public Subject login(final Subject subject, final AuthenticationToken token) {
                if (fail[0]) {
                    throw new IllegalStateException("login failure");
                }
                return super.login(subject, token);
            }
        });
        final ShiroProfileManager manager = new ShiroProfileManager(MockWebContext.create(), new MockSessionStore());
        manager.save(true, profile("id"), false);
        assertTrue(SecurityUtils.getSubject().isAuthenticated());

        fail[0] = true;
        assertThrows(IllegalStateException.class, () -> manager.save(true, profile("otherId"), false));

        assertFalse(SecurityUtils.getSubject().isAuthenticated());
        assertNull(SecurityUtils.getSubject().getPrincipal());
        assertTrue(manager.getProfiles().isEmpty());
    }

    @Test
    public void testExpirationOfTheLastProfileClearsShiroAuthenticationAndPermissions() {
        bind(new DefaultSecurityManager(new Pac4jRealm()));
        final ShiroProfileManager manager = new ShiroProfileManager(MockWebContext.create(), ShiroSessionStore.INSTANCE);
        final ExpiringProfile profile = new ExpiringProfile();
        profile.setId("user");
        profile.setClientName("client");
        profile.addRole("admin");
        manager.save(true, profile, false);
        assertTrue(SecurityUtils.getSubject().hasRole("admin"));
        profile.addAttribute("expired", true);

        assertTrue(manager.getProfiles().isEmpty());
        assertFalse(SecurityUtils.getSubject().isAuthenticated());
        assertNull(SecurityUtils.getSubject().getPrincipals());
        assertNull(SecurityUtils.getSubject().getSession(false));
    }

    @Test
    public void testSessionWriteFailureStillLogsOutAndPreservesTheOriginalException() {
        bind(new DefaultSecurityManager(new Pac4jRealm()));
        final boolean[] fail = { false };
        final IllegalStateException original = new IllegalStateException("save failure");
        final IllegalStateException cleanup = new IllegalStateException("cleanup failure");
        final MockSessionStore store = new MockSessionStore() {
            @Override
            public void set(final WebContext context, final String key, final Object value) {
                if (fail[0]) {
                    if (value instanceof java.util.Map<?, ?> map && map.isEmpty()) {
                        throw cleanup;
                    }
                    throw original;
                }
                super.set(context, key, value);
            }
        };
        final ShiroProfileManager manager = new ShiroProfileManager(MockWebContext.create(), store);
        manager.save(true, profile("user"), false);
        fail[0] = true;

        assertSame(original, assertThrows(IllegalStateException.class, () -> manager.save(true, profile("other"), false)));
        assertArrayEquals(new Throwable[] { cleanup }, original.getSuppressed());
        assertFalse(SecurityUtils.getSubject().isAuthenticated());
        assertNull(SecurityUtils.getSubject().getPrincipals());
    }

    @Test
    public void testFailedProfileRemovalStillLogsOut() {
        bind(new DefaultSecurityManager(new Pac4jRealm()));
        final boolean[] fail = { false };
        final MockSessionStore store = new MockSessionStore() {
            @Override
            public void set(final WebContext context, final String key, final Object value) {
                if (fail[0]) {
                    throw new IllegalStateException("session store unavailable");
                }
                super.set(context, key, value);
            }
        };
        final ShiroProfileManager manager = new ShiroProfileManager(MockWebContext.create(), store);
        manager.save(true, profile("user"), false);
        fail[0] = true;
        assertThrows(IllegalStateException.class, manager::removeProfiles);
        assertFalse(SecurityUtils.getSubject().isAuthenticated());
        assertNull(SecurityUtils.getSubject().getPrincipals());
    }
}
