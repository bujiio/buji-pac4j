package io.buji.oauth;

import org.apache.shiro.SecurityUtils;
import org.scribe.up.session.UserSession;

/**
 * This implementation uses the Shiro session for the user session.
 * 
 * @author Jerome Leleu
 */
public final class ShiroUserSession implements UserSession {
    
    public void setAttribute(String key, Object value) {
        SecurityUtils.getSubject().getSession().setAttribute(key, value);
    }
    
    public Object getAttribute(String key) {
        return SecurityUtils.getSubject().getSession().getAttribute(key);
    }
}
