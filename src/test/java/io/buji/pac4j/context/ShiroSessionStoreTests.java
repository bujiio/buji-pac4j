package io.buji.pac4j.context;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the {@link ShiroSessionStore}.
 *
 * @author Jerome Leleu
 * @since 10.0.1
 */
public final class ShiroSessionStoreTests {

    private static final String KEY = "key";

    private static final String VALUE = "value";

    private final ShiroSessionStore store = ShiroSessionStore.INSTANCE;

    @After
    public void tearDown() {
        ThreadContext.remove();
    }

    @Test
    public void testDestroySessionWithoutSession() {
        ThreadContext.bind(new DefaultSecurityManager());

        assertTrue(store.destroySession(null));

        assertNull(SecurityUtils.getSubject().getSession(false));
    }

    @Test
    public void testDestroySession() {
        ThreadContext.bind(new DefaultSecurityManager());
        store.set(null, KEY, VALUE);
        assertEquals(VALUE, store.get(null, KEY).orElse(null));

        assertTrue(store.destroySession(null));

        assertTrue(store.get(null, KEY).isEmpty());
    }

    @Test
    public void testRemoveValueWithoutSession() {
        ThreadContext.bind(new DefaultSecurityManager());

        store.set(null, KEY, null);

        assertNull(SecurityUtils.getSubject().getSession(false));
    }

    @Test
    public void testRemoveValue() {
        ThreadContext.bind(new DefaultSecurityManager());
        store.set(null, KEY, VALUE);

        store.set(null, KEY, null);

        assertTrue(store.get(null, KEY).isEmpty());
    }

    @Test
    public void testNoSecurityManager() {
        // only tolerated (and logged) when saving a value
        store.set(null, KEY, VALUE);

        assertThrows(UnavailableSecurityManagerException.class, () -> store.get(null, KEY));
        assertThrows(UnavailableSecurityManagerException.class, () -> store.getSessionId(null, true));
        assertThrows(UnavailableSecurityManagerException.class, () -> store.destroySession(null));
    }

    @Test
    public void testDisabledSessionsCanBeReadAndDestroyedWithoutCreatingOne() {
        final DefaultSecurityManager manager = new DefaultSecurityManager();
        try {
            ThreadContext.bind(new org.apache.shiro.subject.Subject.Builder(manager)
                .sessionCreationEnabled(false).buildSubject());
            store.set(null, KEY, VALUE);
            assertTrue(store.get(null, KEY).isEmpty());
            assertTrue(store.getSessionId(null, true).isEmpty());
            assertTrue(store.destroySession(null));
        } finally {
            manager.destroy();
        }
    }
}
