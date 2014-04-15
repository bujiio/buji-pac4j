package io.buji.pac4j;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

/**
 * Factory for building a Shiro subject authenticated by a pac4j client (usually a CasClient).
 * This factory sets the Shiro context as not authenticated if the user was RememberMe authenticated.
 *
 * @author Michael Remond
 * @since 1.2.3
 */
public class ClientSubjectFactory extends DefaultWebSubjectFactory {

    @Override
    public Subject createSubject(SubjectContext context) {

        boolean authenticated = context.isAuthenticated();

        if (authenticated) {

            AuthenticationToken token = context.getAuthenticationToken();

            if (token != null && token instanceof ClientToken) {
                ClientToken clientToken = (ClientToken) token;
                if (clientToken.isRememberMe()) {
                    context.setAuthenticated(false);
                }
            }
        }

        return super.createSubject(context);
    }
}
