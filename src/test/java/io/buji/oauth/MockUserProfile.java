package io.buji.oauth;

import org.scribe.up.profile.UserProfile;

/**
 * This profile is a mock with a mock provider type and no attributes definition.
 * 
 * @author Jerome Leleu
 */
public class MockUserProfile extends UserProfile {
    
    private static final long serialVersionUID = 3079771089205505215L;
    
    public MockUserProfile() {
    }
    
    public MockUserProfile(Object id) {
        super(id);
    }
}
