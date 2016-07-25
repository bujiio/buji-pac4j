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
package io.buji.pac4j.context;

import io.buji.pac4j.util.ShiroHelper;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.CommonProfile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;

/**
 * A specific Shiro web context where the authenticated profiles are saved into the Shiro subject.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class ShiroContext extends J2EContext {

    public ShiroContext(final HttpServletRequest request, final HttpServletResponse response) {
        super(request, response, defaultStore(null));
    }

    public ShiroContext(final HttpServletRequest request, final HttpServletResponse response, final SessionStore<J2EContext> sessionStore) {
        super(request, response, defaultStore(sessionStore));
    }

    private static SessionStore<J2EContext> defaultStore(final SessionStore<J2EContext> sessionStore) {
        if (sessionStore != null) {
            return sessionStore;
        } else {
            return ShiroSessionStore.INSTANCE;
        }
    }

    @Override
    public void setRequestAttribute(final String name, final Object value) {
        if (Pac4jConstants.USER_PROFILES.equals(name)) {
            ShiroHelper.populateSubject((LinkedHashMap<String, CommonProfile>) value);
        }
        super.setRequestAttribute(name, value);
    }

    @Override
    public void setSessionAttribute(final String name, final Object value) {
        if (Pac4jConstants.USER_PROFILES.equals(name)) {
            ShiroHelper.populateSubject((LinkedHashMap<String, CommonProfile>) value);
        }
        super.setSessionAttribute(name, value);
    }
}
