package io.buji.oauth;

import org.apache.shiro.authc.AuthenticationToken;
import org.scribe.up.credential.OAuthCredential;

/**
 * This class represents a token for an OAuth authentication process (OAuth credential + user identifier after authentication).
 * 
 * @author Jerome Leleu
 */
public final class OAuthToken implements AuthenticationToken {
    
    private static final long serialVersionUID = 3376624432421737333L;
    
    private OAuthCredential credential;
    
    private String userId;
    
    public OAuthToken(OAuthCredential credential) {
        this.credential = credential;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Object getPrincipal() {
        return userId;
    }
    
    public Object getCredentials() {
        return credential;
    }
}
