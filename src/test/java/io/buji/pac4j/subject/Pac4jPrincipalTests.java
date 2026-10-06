package io.buji.pac4j.subject;

import org.apache.shiro.lang.io.DefaultSerializer;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.Pac4jConstants;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests {@link Pac4jPrincipal}.
 *
 * @author Jerome Leleu
 * @since 2.0.1
 */
public final class Pac4jPrincipalTests {
    
    private static final String PROFILE_ID = "123";
    private static final String TEST_USERNAME = "superman";
    private static final String TEST_EMAIL = "clark.kent@dailyplanet.org";

    @Test
    public void testSerialize() {
        final List<UserProfile> profiles = new ArrayList<>();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles);

        final DefaultSerializer serializer = new DefaultSerializer();
        final byte[] serialized = serializer.serialize(principal);
        final Pac4jPrincipal principal2 = (Pac4jPrincipal) serializer.deserialize(serialized);
        assertEquals(principal2, principal);
    }
    
    /**
     * A principal (no profile, "email" attribute) serialized with buji-pac4j v10.0.0.
     */
    private static final String V10_0_0_SERIALIZED_PRINCIPAL = "rO0ABXNyACRpby5idWppLnBhYzRqLnN1YmplY3QuUGFjNGpQcmluY2lw"
        + "YWyBoeHGjh+wqQIAAkwAFnByaW5jaXBhbE5hbWVBdHRyaWJ1dGV0ABJMamF2YS9sYW5nL1N0cmluZztMAAhwcm9maWxlc3QAEExqYXZhL3V0aW"
        + "wvTGlzdDt4cHQABWVtYWlsc3IAE2phdmEudXRpbC5BcnJheUxpc3R4gdIdmcdhnQMAAUkABHNpemV4cAAAAAB3BAAAAAB4";

    @Test
    public void testDeserializePreviousVersion() throws Exception {
        final byte[] serialized = Base64.getDecoder().decode(V10_0_0_SERIALIZED_PRINCIPAL);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialized))) {
            final Pac4jPrincipal principal = (Pac4jPrincipal) ois.readObject();
            assertEquals("email", principal.getPrincipalNameAttribute());
            assertTrue(principal.getProfiles().isEmpty());
            assertNotNull(principal.getAuthenticationId());
            assertTrue(principal.hasSameIdentity(new ArrayList<>()));
        }
    }

    @Test 
    public void testNoAttribute() {
        final List<UserProfile> profiles = createProfiles();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles);
        assertEquals(PROFILE_ID, principal.getName());
    }
    
    @Test 
    public void testBlankAttribute() {
        final List<UserProfile> profiles = createProfiles();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles, " ");
        assertEquals(PROFILE_ID, principal.getName());
    }
    
    @Test 
    public void testNullAttribute() {
        final List<UserProfile> profiles = createProfiles();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles, null);
        assertEquals(PROFILE_ID, principal.getName());
    }
    
    @Test 
    public void testLeftPaddedAttribute() {
        final List<UserProfile> profiles = createProfiles();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles, "  " + Pac4jConstants.USERNAME);
        assertEquals(TEST_USERNAME, principal.getName());
    }
    
    @Test 
    public void testRightPaddedAttribute() {
        final List<UserProfile> profiles = createProfiles();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles, Pac4jConstants.USERNAME + " ");
        assertEquals(TEST_USERNAME, principal.getName());
    }
    
    @Test 
    public void testUsernameAttribute() {
        final List<UserProfile> profiles = createProfiles();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles, Pac4jConstants.USERNAME);
        assertEquals(TEST_USERNAME, principal.getName());
    }
    
    @Test 
    public void testEmailAttribute() {
        final List<UserProfile> profiles = createProfiles();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles, "email");
        assertEquals(TEST_EMAIL, principal.getName());
    }
    
    @Test 
    public void testNonExistantAttribute() {
        final List<UserProfile> profiles = createProfiles();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles, "display_name");
        assertNull(principal.getName());
    }
    
    @Test 
    public void testIntegerAttribute() {
        final List<UserProfile> profiles = createProfiles();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles, "age");
        assertEquals(principal.getName(), "21");
    }

    @Test
    public void testMutationDoesNotChangeTheHashOrBreakHashCollections() {
        final List<UserProfile> profiles = createProfiles();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles);
        final Set<Pac4jPrincipal> principals = new HashSet<>();
        principals.add(principal);
        final int hash = principal.hashCode();
        profiles.get(0).addAttribute("access_token", "new-token");
        assertEquals(hash, principal.hashCode());
        assertTrue(principals.contains(principal));
    }

    @Test
    public void testOriginalIdentitySurvivesInPlaceChangesAndSerialization() {
        final List<UserProfile> profiles = createProfiles();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles);
        final DefaultSerializer<Pac4jPrincipal> serializer = new DefaultSerializer<>();
        final Pac4jPrincipal restored = serializer.deserialize(serializer.serialize(principal));
        assertEquals(principal.getAuthenticationId(), restored.getAuthenticationId());
        assertEquals(principal, restored);
        assertTrue(restored.hasSameIdentity(profiles));
        ((CommonProfile) profiles.get(0)).setId("other-user");
        assertFalse(principal.hasSameIdentity(profiles));
        assertFalse(restored.hasSameIdentity(profiles));
    }

    @Test
    public void testTwoLoginsOfTheSameUserAreDifferentPrincipals() {
        final List<UserProfile> profiles = createProfiles();
        assertNotEquals(new Pac4jPrincipal(profiles), new Pac4jPrincipal(profiles));
    }

    @Test
    public void testNoProfileHasNoName() {
        final Pac4jPrincipal principal = new Pac4jPrincipal(new ArrayList<>());
        assertNull(principal.getProfile());
        assertNull(principal.getName());
        assertEquals("", principal.toString());
        assertNull(new Pac4jPrincipal(null).getProfile());
    }
    
    private static List<UserProfile> createProfiles() {
        final CommonProfile profile = new CommonProfile();
        profile.setId(PROFILE_ID);
        profile.addAttribute(Pac4jConstants.USERNAME, TEST_USERNAME);
        profile.addAttribute("family_name", "Kent");
        profile.addAttribute("first_name", "Clark");
        profile.addAttribute("email", TEST_EMAIL);
        profile.addAttribute("age", 21);
        final List<UserProfile> profiles = new ArrayList<>();
        profiles.add(profile);
        return profiles;
    }
}
