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
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.OAuthProvider;
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
    
    // the provider which is used for delegating authentication (Facebook, Twitter...)
    private OAuthProvider provider;
    
    // default roles applied to authenticated user
    private String defaultRoles;
    
    // default permissions applied to authenticated user
    private String defaultPermissions;
    
    public OAuthRealm() {
        setAuthenticationTokenClass(OAuthToken.class);
    }
    
    /**
     * Authenticates a user and retrieves its user profile.
     * 
     * @param authenticationToken the authentication token
     * @throws AuthenticationException if there is an error during authentication.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
        throws AuthenticationException {
        OAuthToken oauthToken = (OAuthToken) authenticationToken;
        // token must be provided
        if (oauthToken == null) {
            return null;
        }
        
        // OAuth credential
        OAuthCredential credential = (OAuthCredential) oauthToken.getCredentials();
        log.debug("credential : {}", credential);
        
        // credential should be not null and for the right provider
        if (credential == null || !provider.getType().equals(credential.getProviderType())) {
            return null;
        }
        
        UserProfile userProfile = null;
        // finish OAuth authentication process : get the user profile
        userProfile = provider.getUserProfile(credential);
        log.debug("userProfile : {}", userProfile);
        if (userProfile == null || !StringUtils.hasText(userProfile.getId())) {
            log.error("Unable to get user profile for OAuth credentials : [{}]", credential);
            throw new OAuthAuthenticationException("Unable to get user profile for OAuth credential : [" + credential
                                                   + "]");
        }
        
        // refresh authentication token with user id
        String userId = userProfile.getTypedId();
        oauthToken.setUserId(userId);
        // create simple authentication info
        List<? extends Object> principals = CollectionUtils.asList(userId, userProfile);
        PrincipalCollection principalCollection = new SimplePrincipalCollection(principals, getName());
        return new SimpleAuthenticationInfo(principalCollection, credential);
    }
    
    /**
     * Retrieves the AuthorizationInfo for the given principals.
     * 
     * @param principals the primary identifying principals of the AuthorizationInfo that should be retrieved.
     * @return the AuthorizationInfo associated with this principals.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // create simple authorization info
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        // add default roles
        simpleAuthorizationInfo.addRoles(split(defaultRoles));
        // add default permissions
        simpleAuthorizationInfo.addStringPermissions(split(defaultPermissions));
        return simpleAuthorizationInfo;
    }
    
    /**
     * Split a string into a list of not empty and trimmed strings, delimiter is a comma.
     * 
     * @param s the input string
     * @return the list of not empty and trimmed strings
     */
    protected List<String> split(String s) {
        List<String> list = new ArrayList<String>();
        String[] elements = StringUtils.split(s, ',');
        if (elements != null && elements.length > 0) {
            for (String element : elements) {
                if (StringUtils.hasText(element)) {
                    list.add(element.trim());
                }
            }
        }
        return list;
    }
    
    public void setProvider(OAuthProvider provider) {
        this.provider = provider;
    }
    
    public void setDefaultRoles(String defaultRoles) {
        this.defaultRoles = defaultRoles;
    }
    
    public void setDefaultPermissions(String defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }
}
