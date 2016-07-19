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

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * The principal to store the pac4j profiles.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class Pac4jPrincipal {

    private final LinkedHashMap<String, CommonProfile> profiles;

    public Pac4jPrincipal(final LinkedHashMap<String, CommonProfile> profiles) {
        this.profiles = profiles;
    }

    /**
     * Get the main profile of the authenticated user.
     *
     * @return the main profile
     */
    public CommonProfile getProfile() {
        return ProfileHelper.flatIntoOneProfile(this.profiles).get();
    }

    /**
     * Get all the profiles of the authenticated user.
     *
     * @return the list of profiles
     */
    public List<CommonProfile> getProfiles() {
        return ProfileHelper.flatIntoAProfileList(this.profiles);
    }
}
