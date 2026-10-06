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

import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A principal created by Pac4JRealm that wraps a pac4j UserProfile.
 * 
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class Pac4jPrincipal implements Principal, Serializable {

    /**
     * The value computed for the v10.0.0 class: keep it to deserialize the principals saved by the previous versions.
     */
    private static final long serialVersionUID = -9105748728662216535L;

    /**
     * All the profiles of the authenticated user. They are updated when the profiles are renewed (after a refresh
     * token exchange for example) without any new Shiro authentication.
     */
    @Getter
    @Setter
    private List<UserProfile> profiles;
    /**
     * The principal name attribute.
     */
    @Getter
    private final String principalNameAttribute;

    /**
     * A stable identifier for this authentication, including across session serialization and profile renewal.
     */
    @Getter
    private String authenticationId = UUID.randomUUID().toString();

    /**
     * Clients and identifiers captured at login, independently of later changes to the mutable profiles.
     */
    private List<List<String>> profileIdentities;

    /**
     * Construct a Pac4jPrincipal.  The principal name returned will be 
     * CommonProfile.getId().
     * 
     * @param profiles A list containing all of the CommonProfiles created by Pac4j
     *          authorization.
     */
    public Pac4jPrincipal(final List<UserProfile> profiles) {
        this(profiles, null);
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
    public Pac4jPrincipal(final List<UserProfile> profiles, final String principalNameAttribute) {
        this.profiles = profiles;
        this.principalNameAttribute = CommonHelper.isBlank(principalNameAttribute) ?
                                        null : principalNameAttribute.trim();
        this.profileIdentities = identities(profiles);
    }

    /**
     * Get the main profile of the authenticated user.
     *
     * @return the main profile
     */
    public UserProfile getProfile() {
        return profiles == null ? null : ProfileHelper.flatIntoOneProfile(profiles).orElse(null);
    }

    /**
     * Compare new profiles with the identity captured at login, even if the old profiles have already been mutated.
     *
     * @param newProfiles the new profiles
     * @return whether the clients and identifiers still match the original authentication
     */
    public boolean hasSameIdentity(final List<UserProfile> newProfiles) {
        return Objects.equals(profileIdentities, identities(newProfiles));
    }

    private static List<List<String>> identities(final List<UserProfile> profiles) {
        if (profiles == null) {
            return null;
        }
        final List<List<String>> result = new ArrayList<>();
        for (final UserProfile profile : profiles) {
            result.add(profile == null ? null : Arrays.asList(profile.getClientName(), profile.getId()));
        }
        return result;
    }

    /**
     * Restore authentication identity fields absent from sessions written by v10.0.0.
     *
     * @param input the serialized principal
     * @throws IOException if the principal cannot be read
     * @throws ClassNotFoundException if a serialized class is unavailable
     */
    private void readObject(final ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject();
        // These fields are absent in sessions written by v10.0.0.
        if (authenticationId == null) {
            authenticationId = UUID.randomUUID().toString();
        }
        if (profileIdentities == null) {
            profileIdentities = identities(profiles);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Pac4jPrincipal that = (Pac4jPrincipal) o;
        // Different logins must differ: Shiro uses equals to decide whether to save new principals in the session.
        return authenticationId.equals(that.authenticationId);
    }

    @Override
    public int hashCode() {
        return authenticationId.hashCode();
    }

    @Override
    public String toString() {
        return Objects.toString(getName(), "");
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
        if (profile == null) {
            return null;
        }
        if (null == principalNameAttribute) {
            return profile.getId();
        }
        final Object attrValue = profile.getAttribute(principalNameAttribute);
        return (null == attrValue) ? null : String.valueOf(attrValue);
    }
}
