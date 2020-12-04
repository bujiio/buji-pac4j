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

import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;

/**
 * A principal created by Pac4JRealm that wraps a CommonProfile.
 * 
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class Pac4jPrincipal implements Principal, Serializable {

    private final String principalNameAttribute;
    private final List<UserProfile> profiles;

    /**
     * Construct a Pac4jPrincipal.  The principal name returned will be 
     * CommonProfile.getId().
     * 
     * @param profiles A list containing all of the CommonProfiles created by Pac4j
     *          authorization.
     */
    public Pac4jPrincipal(final List<UserProfile> profiles) {
        this.profiles = profiles;
        this.principalNameAttribute = null;
    }
    
    /**
     * Construct a Pac4jPrincipal and specify which attribute in the CommonProfile
     * should be used for the principal name.
     * 
     * @param profiles A list containing all of the CommonProfiles created by Pac4j
     *          authorization.
     * @param principalNameAttribute The attribute name in the CommonProfile that 
     *          holds the principal name. A null or blank value means
     *          that CommonProfile.getId() should be used as the principal name.
     */
    public Pac4jPrincipal(final List<UserProfile> profiles, String principalNameAttribute) {
        this.profiles = profiles;
        this.principalNameAttribute = CommonHelper.isBlank(principalNameAttribute) ?
                                        null : principalNameAttribute.trim();
    }

    /**
     * Get the main profile of the authenticated user.
     *
     * @return the main profile
     */
    public UserProfile getProfile() {
        return ProfileHelper.flatIntoOneProfile(this.profiles).get();
    }

    /**
     * Get all the profiles of the authenticated user.
     *
     * @return the list of profiles
     */
    public List<UserProfile> getProfiles() {
        return this.profiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Pac4jPrincipal that = (Pac4jPrincipal) o;
        return profiles != null ? profiles.equals(that.profiles) : that.profiles == null;
    }

    @Override
    public int hashCode() {
        return profiles != null ? profiles.hashCode() : 0;
    }

    /**
     * Returns a name for the principal based upon one of the attributes
     * of the main CommonProfile.  The attribute name used to query the CommonProfile 
     * is specified in the constructor. 
     * 
     * @return a name for the Principal or null if the attribute is not populated.
     */
    @Override
    public String getName() {
        final UserProfile profile = this.getProfile();
        if (null == principalNameAttribute) {
            return profile.getId();
        }
        final Object attrValue = profile.getAttribute(principalNameAttribute);
        return (null == attrValue) ? null : String.valueOf(attrValue);
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "profiles", getProfiles());
    }
}
