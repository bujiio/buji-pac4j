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
package io.buji.pac4j.profile;

import io.buji.pac4j.util.ShiroHelper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;

import java.util.LinkedHashMap;

/**
 * Specific profile manager for Shiro.
 *
 * @author Jerome Leleu
 * @since 2.0.2
 */
public class ShiroProfileManager extends ProfileManager {

    public ShiroProfileManager(final WebContext context, final SessionStore sessionStore) {
        super(context, sessionStore);
    }

    @Override
    protected void saveAll(LinkedHashMap<String, UserProfile> profiles, final boolean saveInSession) {
        super.saveAll(profiles, saveInSession);

        try {
            ShiroHelper.populateSubject(profiles);
        } catch (final AuthenticationException e) {
            super.removeProfiles();
            throw e;
        }
    }

    @Override
    public void removeProfiles() {
        super.removeProfiles();

        SecurityUtils.getSubject().logout();
    }
}
