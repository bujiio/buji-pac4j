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
package io.buji.pac4j.realm;

import io.buji.pac4j.subject.Pac4jPrincipal;
import io.buji.pac4j.token.Pac4jToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.pac4j.core.profile.CommonProfile;

import java.util.*;

/**
 * Realm based on pac4j token (authentication has already occurred).
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class Pac4jRealm extends AuthorizingRealm {
    
    private String principalNameAttribute;

    public Pac4jRealm() {
        setAuthenticationTokenClass(Pac4jToken.class);
    }

    /**
     * Returns the name of the attribute from CommonProfile that will be used as
     * the value for the principal name.
     * 
     * @return an attribute name or null
     */
    public String getPrincipalNameAttribute() {
        return principalNameAttribute;
    }

    /**
     * Sets the name of the attribute from the CommonProfile that should be returned
     * as the principal name. Common attribute names include "email", "username" or "display_name"
     * but valid values are ultimately determined by the Pac4j client and the profile 
     * that it returns. A null or blank string provided for the attribute name will
     * cause the principal to return CommonProfile.getId() as the name.
     * 
     * @param principalNameAttribute The attribute name to return. Null or blank will
     *          result in CommonProfile.getId() being used for the principal name.
     * @see Pac4jPrincipal#getName()
     * @see CommonProfile
     */
    public void setPrincipalNameAttribute(String principalNameAttribute) {
        this.principalNameAttribute = principalNameAttribute;
    }
    
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken authenticationToken)
            throws AuthenticationException {

        final Pac4jToken token = (Pac4jToken) authenticationToken;
        final LinkedHashMap<String, CommonProfile> profiles = token.getProfiles();

        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles, principalNameAttribute);
        final PrincipalCollection principalCollection = new SimplePrincipalCollection(principal, getName());
        return new SimpleAuthenticationInfo(principalCollection, profiles.hashCode());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {
        final Set<String> roles = new HashSet<>();
        final Set<String> permissions = new HashSet<>();
        final Pac4jPrincipal principal = principals.oneByType(Pac4jPrincipal.class);
        if (principal != null) {
            final List<CommonProfile> profiles = principal.getProfiles();
            for (CommonProfile profile : profiles) {
                if (profile != null) {
                    roles.addAll(profile.getRoles());
                    permissions.addAll(profile.getPermissions());
                }
            }
        }

        final SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRoles(roles);
        simpleAuthorizationInfo.addStringPermissions(permissions);
        return simpleAuthorizationInfo;
    }
}
