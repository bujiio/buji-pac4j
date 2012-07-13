package io.buji.oauth;

import java.util.Map;

import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.session.UserSession;

/**
 * This provider is a mock with specific behaviours for tests.
 * 
 * @author Jerome Leleu
 */
public final class MockOAuthProvider implements OAuthProvider {
    
    public final static String TYPE = "mockType";
    
    public final static String NULL_USER_PROFILE = "nullUserProfile";
    
    public final static String USER_PROFILE_WITH_EMPTY_ID = "userProfileWithEmptyId";
    
    public final static String USER_PROFILE_WITHOUT_ATTRIBUTE = "userProfileWithoutAttribute";
    
    public final static String USER_PROFILE_WITH_ONE_ATTRIBUTE = "userProfileWithOneAttribute";
    
    public final static String ATTRIBUTE_KEY = "attributeKey";
    
    public final static String ATTRIBUTE_VALUE = "attributeValue";
    
    public String getType() {
        return TYPE;
    }
    
    public String getAuthorizationUrl(UserSession userSession) {
        return null;
    }
    
    public UserProfile getUserProfile(OAuthCredential credential) {
        String token = credential.getToken();
        if (NULL_USER_PROFILE.equals(token)) {
            return null;
        } else if (USER_PROFILE_WITH_EMPTY_ID.equals(token)) {
            return new MockUserProfile();
        } else if (USER_PROFILE_WITHOUT_ATTRIBUTE.equals(token)) {
            return new MockUserProfile(token);
        } else if (USER_PROFILE_WITH_ONE_ATTRIBUTE.equals(token)) {
            MockUserProfile userProfile = new MockUserProfile(token);
            userProfile.addAttribute(ATTRIBUTE_KEY, ATTRIBUTE_VALUE);
            return userProfile;
        }
        throw new IllegalStateException();
    }
    
    public OAuthCredential getCredential(UserSession userSession, Map<String, String[]> parameters) {
        return null;
    }
}
