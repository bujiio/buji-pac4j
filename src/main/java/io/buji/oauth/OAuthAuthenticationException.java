package io.buji.oauth;

import org.apache.shiro.authc.AuthenticationException;

/**
 * This class is the exception that is thrown when OAuth authentication process fails in OAuthRealm.
 * 
 * @author Jerome Leleu
 */
public final class OAuthAuthenticationException extends AuthenticationException {
    
    private static final long serialVersionUID = -9060319148695558222L;
    
    public OAuthAuthenticationException() {
        super();
    }
    
    public OAuthAuthenticationException(String message) {
        super(message);
    }
    
    public OAuthAuthenticationException(Throwable cause) {
        super(cause);
    }
    
    public OAuthAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
