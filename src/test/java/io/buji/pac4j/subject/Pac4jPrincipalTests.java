package io.buji.pac4j.subject;

import org.apache.shiro.io.DefaultSerializer;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.Pac4jConstants;

import java.util.ArrayList;
import java.util.List;

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
