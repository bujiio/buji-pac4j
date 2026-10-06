package io.buji.pac4j.realm;

import io.buji.pac4j.subject.Pac4jPrincipal;
import io.buji.pac4j.token.Pac4jToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests the {@link Pac4jRealm}.
 *
 * @author Jerome Leleu
 * @since 10.0.1
 */
public final class Pac4jRealmTests {

    private static AuthorizationInfo authorizationInfo(final Object permissions) {
        final CommonProfile profile = new CommonProfile();
        profile.setId("id");
        profile.addRole("role");
        if (permissions != null) {
            profile.addAttribute(Pac4jRealm.SHIRO_PERMISSIONS, permissions);
        }
        final Pac4jPrincipal principal = new Pac4jPrincipal(List.of(profile));
        return new Pac4jRealm().doGetAuthorizationInfo(new SimplePrincipalCollection(principal, "realm"));
    }

    @Test
    public void testListPermissions() {
        final AuthorizationInfo info = authorizationInfo(Arrays.asList("perm1", "perm2"));
        assertEquals(Set.of("role"), info.getRoles());
        assertEquals(Set.of("perm1", "perm2"), info.getStringPermissions());
    }

    @Test
    public void testSetPermissions() {
        final AuthorizationInfo info = authorizationInfo(new LinkedHashSet<>(List.of("perm1")));
        assertEquals(Set.of("perm1"), info.getStringPermissions());
    }

    @Test
    public void testOtherCollectionPermissions() {
        final AuthorizationInfo info = authorizationInfo(new ArrayDeque<>(List.of("perm1", "perm2")));
        assertEquals(Set.of("perm1", "perm2"), info.getStringPermissions());
    }

    @Test
    public void testArrayPermissions() {
        final AuthorizationInfo info = authorizationInfo(new String[] { "perm1", "perm2" });
        assertEquals(Set.of("perm1", "perm2"), info.getStringPermissions());
    }

    @Test
    public void testSinglePermission() {
        final AuthorizationInfo info = authorizationInfo("perm1");
        assertEquals(Set.of("perm1"), info.getStringPermissions());
    }

    @Test
    public void testObjectPermissions() {
        final WildcardPermission permission = new WildcardPermission("printer:print");
        final AuthorizationInfo info = authorizationInfo(Arrays.asList("perm1", permission, null));
        assertEquals(Set.of("perm1"), info.getStringPermissions());
        assertEquals(Set.of(permission), info.getObjectPermissions());
    }

    @Test
    public void testNoPermission() {
        final AuthorizationInfo info = authorizationInfo(null);
        assertEquals(Set.of("role"), info.getRoles());
        assertTrue(info.getStringPermissions() == null || info.getStringPermissions().isEmpty());
    }

    @Test
    public void testInvalidProfilesAreRejectedWithAuthenticationExceptions() {
        final Pac4jRealm realm = new Pac4jRealm();
        assertThrows(AuthenticationException.class, () -> realm.getAuthenticationInfo(new Pac4jToken(null, false)));
        assertThrows(AuthenticationException.class, () -> realm.getAuthenticationInfo(new Pac4jToken(List.of(), false)));
        assertThrows(AuthenticationException.class,
            () -> realm.getAuthenticationInfo(new Pac4jToken(List.of(new CommonProfile()), false)));
    }

    @Test
    public void testUnsupportedPermissionTypesCannotGrantTheirStringRepresentation() {
        final AuthorizationInfo info = authorizationInfo(List.of(42, new Object() {
            @Override
            public String toString() {
                return "*";
            }
        }));
        assertTrue(info.getStringPermissions() == null || info.getStringPermissions().isEmpty());
    }
}
