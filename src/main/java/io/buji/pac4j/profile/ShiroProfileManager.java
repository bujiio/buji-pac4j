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
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;

/**
 * Specific profile manager for Shiro.
 *
 * @author Jerome Leleu
 * @since 2.0.2
 */
public class ShiroProfileManager extends ProfileManager<CommonProfile> {

    public ShiroProfileManager(final WebContext context) {
        super(context);
    }

    @Override
    public void save(final boolean saveInSession, final CommonProfile profile, final boolean multiProfile) {
        super.save(saveInSession, profile, multiProfile);

        ShiroHelper.populateSubject(retrieveAll(saveInSession));
    }

    @Override
    public void remove(final boolean removeFromSession) {
        super.remove(removeFromSession);

        SecurityUtils.getSubject().logout();
    }
}
