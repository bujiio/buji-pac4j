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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This realm implementation is dedicated to authentication. It acts on
 * credentials after the user authenticates at the provider and finishes the
 * authentication process by getting the user profile from the provider.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class ClientRealm extends AuthorizingRealm {

    private static Logger log = LoggerFactory.getLogger(ClientRealm.class);

    // the clients definition
    private Clients clients;

    // default roles applied to authenticated user
    private String defaultRoles;

    // default permissions applied to authenticated user
    private String defaultPermissions;

    public ClientRealm() {
        setAuthenticationTokenClass(ClientToken.class);
    }

    /**
     * Authenticates a user and retrieves its user profile.
     *
     * @param authenticationToken
     *            the authentication token
     * @throws AuthenticationException
     *             if there is an error during authentication.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken authenticationToken)
            throws AuthenticationException {
        try {
            return internalGetAuthenticationInfo(authenticationToken);
        } catch (final TechnicalException e) {
            throw new AuthenticationException(e);
        }
    }

    /**
     * Authenticates a user and retrieves its user profile.
     *
     * @param authenticationToken the authentication token
     * @return authentication information
     */
    @SuppressWarnings("unchecked")
    protected AuthenticationInfo internalGetAuthenticationInfo(final AuthenticationToken authenticationToken) {
        final ClientToken clientToken = (ClientToken) authenticationToken;
        log.debug("clientToken : {}", clientToken);
        // token must be provided
        if (clientToken == null) {
            return null;
        }

        // credentials
        final Credentials credentials = (Credentials) clientToken.getCredentials();
        log.debug("credentials : {}", credentials);

        // client
        final Client<Credentials, CommonProfile> client = this.clients.findClient(clientToken.getClientName());
        log.debug("client : {}", client);

        // finish authentication process : get the user profile
        final CommonProfile profile = client.getUserProfile(credentials, null);
        log.debug("profile : {}", profile);
        // no profile
        if (profile == null) {
            final String message = "No profile retrieved from authentication using client : " + client
                    + " and credentials : " + credentials;
            log.info(message);
            throw new NoAuthenticationException(message);
        }

        // refresh authentication token with user id
        final String userId = profile.getTypedId();
        clientToken.setUserId(userId);
        // set rememberMe status
        clientToken.setRememberMe(profile.isRemembered());
        // create simple authentication info
        final List<? extends Object> principals = CollectionUtils.asList(userId, profile);
        final PrincipalCollection principalCollection = new SimplePrincipalCollection(principals, getName());
        return new SimpleAuthenticationInfo(principalCollection, credentials);
    }

    /**
     * Retrieves the AuthorizationInfo for the given principals.
     *
     * @param principals
     *            the primary identifying principals of the AuthorizationInfo
     *            that should be retrieved.
     * @return the AuthorizationInfo associated with this principals.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {
        Set<String> roles = new HashSet<String>(split(this.defaultRoles));
        Set<String> permissions = new HashSet<String>(split(this.defaultPermissions));
        // get roles and permissions from principals
        Collection<CommonProfile> profiles = principals.byType(CommonProfile.class);
        if (profiles != null) {
            for (CommonProfile profile : profiles) {
                if (profile != null) {
                    roles.addAll(profile.getRoles());
                    permissions.addAll(profile.getPermissions());
                }
            }
        }
        // create simple authorization info
        final SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRoles(roles);
        simpleAuthorizationInfo.addStringPermissions(permissions);
        return simpleAuthorizationInfo;
    }

    /**
     * Split a string into a list of not empty and trimmed strings, delimiter is
     * a comma.
     *
     * @param s
     *            the input string
     * @return the list of not empty and trimmed strings
     */
    protected List<String> split(final String s) {
        final List<String> list = new ArrayList<String>();
        final String[] elements = StringUtils.split(s, ',');
        if (elements != null && elements.length > 0) {
            for (final String element : elements) {
                if (StringUtils.hasText(element)) {
                    list.add(element.trim());
                }
            }
        }
        return list;
    }

    public Clients getClients() {
        return this.clients;
    }

    public void setClients(final Clients clients) throws TechnicalException {
        this.clients = clients;
        this.clients.init();
    }

    public String getDefaultRoles() {
        return this.defaultRoles;
    }

    public void setDefaultRoles(final String defaultRoles) {
        this.defaultRoles = defaultRoles;
    }

    public String getDefaultPermissions() {
        return this.defaultPermissions;
    }

    public void setDefaultPermissions(final String defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }
}
