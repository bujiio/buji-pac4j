/*
 * Licensed to the bujiio organization of the Shiro project under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.buji.pac4j.subject;

import io.buji.pac4j.token.Pac4jToken;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.subject.support.DelegatingSubject;
import org.apache.shiro.web.subject.WebSubject;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * Factory for building a Shiro subject authenticated by pac4j.
 * This factory sets the Shiro context as not authenticated if the user was RememberMe authenticated.
 *
 * @author Michael Remond
 * @since 1.2.3
 */
public class Pac4jSubjectFactory extends DefaultWebSubjectFactory {

    @Override
    public Subject createSubject(final SubjectContext context) {

        // resolveAuthenticated falls back to the previous session even when the context explicitly says false.
        // A remembered pac4j login must override that old authenticated state before Shiro saves the new subject.
        final boolean authenticated = !isRemembered(context.getAuthenticationToken()) && context.resolveAuthenticated();

        // Mirror Shiro's web/non-web selection, while respecting disabled session creation.
        boolean existingSubjectIsNonWeb = context.getSubject() != null && !(context.getSubject() instanceof WebSubject);
        if (context instanceof WebSubjectContext webContext && !existingSubjectIsNonWeb) {
            return new RememberedWebSubject(context.resolvePrincipals(), authenticated, context.resolveHost(),
                context.resolveSession(), context.isSessionCreationEnabled(), webContext.resolveServletRequest(),
                webContext.resolveServletResponse(), context.resolveSecurityManager());
        }
        return new RememberedSubject(context.resolvePrincipals(), authenticated, context.resolveHost(),
            context.resolveSession(), context.isSessionCreationEnabled(), context.resolveSecurityManager());
    }

    private static boolean isRemembered(final AuthenticationToken token) {
        return token instanceof Pac4jToken pac4jToken && pac4jToken.isRememberMe();
    }

    private static final class RememberedSubject extends DelegatingSubject {

        private RememberedSubject(final PrincipalCollection principals, final boolean authenticated, final String host,
                                  final Session session, final boolean sessionEnabled, final SecurityManager securityManager) {
            super(principals, authenticated, host, session, sessionEnabled, securityManager);
        }

        @Override
        public void login(final AuthenticationToken token) {
            super.login(token);
            // Shiro's login sets this flag to true even when the factory returned a remembered subject.
            if (Pac4jSubjectFactory.isRemembered(token)) {
                authenticated = false;
            }
        }
    }

    private static final class RememberedWebSubject extends WebDelegatingSubject {

        private RememberedWebSubject(final PrincipalCollection principals, final boolean authenticated, final String host,
                                     final Session session, final boolean sessionEnabled, final ServletRequest request,
                                     final ServletResponse response, final SecurityManager securityManager) {
            super(principals, authenticated, host, session, sessionEnabled, request, response, securityManager);
        }

        @Override
        public void login(final AuthenticationToken token) {
            super.login(token);
            if (Pac4jSubjectFactory.isRemembered(token)) {
                authenticated = false;
            }
        }
    }
}
