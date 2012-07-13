package io.buji.oauth;

import io.buji.oauth.OAuthToken;
import junit.framework.TestCase;

import org.scribe.up.credential.OAuthCredential;

/**
 * This class tests the OAuthToken.
 * 
 * @author Jerome Leleu
 */
public final class OAuthTokenTest extends TestCase {
    
    private static final String USER_ID = "userId";
    
    public void testToken() {
        OAuthCredential credential = new OAuthCredential(null, null, null, null);
        OAuthToken oauthToken = new OAuthToken(credential);
        assertEquals(null, oauthToken.getPrincipal());
        oauthToken.setUserId(USER_ID);
        assertEquals(USER_ID, (String) oauthToken.getPrincipal());
        assertEquals(credential, oauthToken.getCredentials());
    }
}
