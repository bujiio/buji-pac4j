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
package io.buji.pac4j.session;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specific session store.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class ShiroSessionStore implements SessionStore<J2EContext> {

    private final static Logger log = LoggerFactory.getLogger(ShiroSessionStore.class);

    public final static ShiroSessionStore INSTANCE = new ShiroSessionStore();

    private ShiroSessionStore() {}

    @Override
    public String getOrCreateSessionId(final J2EContext context) {
        return SecurityUtils.getSubject().getSession().getId().toString();
    }

    @Override
    public Object get(final J2EContext context, final String key) {
        return SecurityUtils.getSubject().getSession().getAttribute(key);
    }

    @Override
    public void set(final J2EContext context, final String key, final Object value) {
        try {
            SecurityUtils.getSubject().getSession().setAttribute(key, value);
        } catch (final UnavailableSecurityManagerException e) {
            log.warn("Should happen just once at startup in some specific case of Shiro Spring configuration", e);
        }
    }
}
