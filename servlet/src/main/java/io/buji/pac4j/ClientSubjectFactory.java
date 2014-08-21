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
