package io.buji.oauth.filter;

import io.buji.oauth.ShiroUserSession;

import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.scribe.up.provider.OAuthProvider;

/**
 * This class specializes the PermissionsAuthorizationFilter to have a login url which is the authorization url of the OAuth provider.
 * 
 * @author Jerome Leleu
 */
public final class OAuthPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter {
    
    private OAuthProvider provider;
    
    private ShiroUserSession shiroUserSession = new ShiroUserSession();
    
    public String getLoginUrl() {
        return provider.getAuthorizationUrl(shiroUserSession);
    }
    
    public void setProvider(OAuthProvider provider) {
        this.provider = provider;
    }
}
