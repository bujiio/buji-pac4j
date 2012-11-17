/*
 * Licensed to the Apache Software Foundation (ASF) under one
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
package io.buji.oauth;

import java.util.ArrayList;
import java.util.List;

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
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.ProfileHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.ProvidersDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This realm implementation is dedicated to OAuth authentication. It acts on OAuth credential after user authenticates at the OAuth
 * provider (Facebook, Twitter...) and finishes the OAuth authentication process by getting the user profile from the OAuth provider.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class OAuthRealm extends AuthorizingRealm {
    
    private static Logger log = LoggerFactory.getLogger(OAuthRealm.class);
    
    // the providers definition
    private ProvidersDefinition providersDefinition;
    
    // default roles applied to authenticated user
    private String defaultRoles;
    
    // default permissions applied to authenticated user
    private String defaultPermissions;
    
    public OAuthRealm() {
        setAuthenticationTokenClass(OAuthToken.class);
        // optimization for CPU / memory consumption
        ProfileHelper.setKeepRawData(false);
    }
    
    /**
     * Authenticates a user and retrieves its user profile.
     * 
     * @param authenticationToken the authentication token
     * @throws AuthenticationException if there is an error during authentication.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken authenticationToken)
        throws AuthenticationException {
        final OAuthToken oauthToken = (OAuthToken) authenticationToken;
        log.debug("oauthToken : {}", oauthToken);
        // token must be provided
        if (oauthToken == null) {
            return null;
        }
        
        // OAuth credential
        final OAuthCredential credential = (OAuthCredential) oauthToken.getCredentials();
        log.debug("credential : {}", credential);
        // credential should be not null
        if (credential == null) {
            return null;
        }

        // OAuth provider
        OAuthProvider provider = providersDefinition.findProvider(credential.getProviderType());
        log.debug("provider : {}", provider);
        // no provider found
        if (provider == null) {
            return null;
        }
        
        // finish OAuth authentication process : get the user profile
         UserProfile userProfile = provider.getUserProfile(credential);
        log.debug("userProfile : {}", userProfile);
        if (userProfile == null || !StringUtils.hasText(userProfile.getId())) {
            log.error("Unable to get user profile for OAuth credentials : [{}]", credential);
            throw new OAuthAuthenticationException("Unable to get user profile for OAuth credential : [" + credential
                                                   + "]");
        }
        
        // refresh authentication token with user id
        final String userId = userProfile.getTypedId();
        oauthToken.setUserId(userId);
        // create simple authentication info
        final List<? extends Object> principals = CollectionUtils.asList(userId, userProfile);
        final PrincipalCollection principalCollection = new SimplePrincipalCollection(principals, getName());
        return new SimpleAuthenticationInfo(principalCollection, credential);
    }
    
    /**
     * Retrieves the AuthorizationInfo for the given principals.
     * 
     * @param principals the primary identifying principals of the AuthorizationInfo that should be retrieved.
     * @return the AuthorizationInfo associated with this principals.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {
        // create simple authorization info
        final SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        // add default roles
        simpleAuthorizationInfo.addRoles(split(this.defaultRoles));
        // add default permissions
        simpleAuthorizationInfo.addStringPermissions(split(this.defaultPermissions));
        return simpleAuthorizationInfo;
    }
    
    /**
     * Split a string into a list of not empty and trimmed strings, delimiter is a comma.
     * 
     * @param s the input string
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
    
    public void setProvider(OAuthProvider provider) {
        providersDefinition = new ProvidersDefinition(provider);
        providersDefinition.init();
    }

	public void setProvidersDefinition(ProvidersDefinition providersDefinition) {
        this.providersDefinition = providersDefinition;
        this.providersDefinition.init();
	}

	public void setDefaultRoles(final String defaultRoles) {
        this.defaultRoles = defaultRoles;
    }
    
    public void setDefaultPermissions(final String defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }
}
