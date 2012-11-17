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

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.ProfileHelper;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.ProvidersDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This filter retrieves OAuth credential after user authenticates at the OAuth provider to create an OAuthToken to finish the OAuth
 * authentication process and retrieve the user profile.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuthFilter extends AuthenticatingFilter {
    
    private static Logger log = LoggerFactory.getLogger(OAuthFilter.class);
    
    // the url where the application is redirected if the OAuth authentication fails
    private String failureUrl;
    
    // the providers definition
    private ProvidersDefinition providersDefinition;
    
    private ShiroUserSession shiroUserSession = new ShiroUserSession();
    
    public OAuthFilter() {
        // optimization for CPU / memory consumption
        ProfileHelper.setKeepRawData(false);
    }

    /**
     * The token created for this authentication is an OAuthToken containing the OAuth credential received after authentication at the OAuth
     * provider. These information are received on the callback url (on which the filter must be configured).
     * 
     * @param request the incoming request
     * @param response the outgoing response
     * @throws Exception if there is an error processing the request.
     */
    @SuppressWarnings("unchecked")
	@Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        Map<String, String[]> parameters = request.getParameterMap();
        OAuthProvider provider = providersDefinition.findProvider(parameters);
        log.debug("provider : {}", provider);
        OAuthCredential credential = provider.getCredential(shiroUserSession, parameters);
        log.debug("credential : {}", credential);
        return new OAuthToken(credential);
    }
    
    /**
     * Execute login by creating {@link #createToken(javax.servlet.ServletRequest, javax.servlet.ServletResponse) token} and logging subject
     * with this token.
     * 
     * @param request the incoming request
     * @param response the outgoing response
     * @throws Exception if there is an error processing the request.
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return executeLogin(request, response);
    }
    
    /**
     * Returns <code>false</code> to always force authentication (user is never considered authenticated by this filter).
     * 
     * @param request the incoming request
     * @param response the outgoing response
     * @param mappedValue the filter-specific config value mapped to this filter in the URL rules mappings.
     * @return <code>false</code>
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return false;
    }
    
    /**
     * If login has been successful, redirect user to the original protected url.
     * 
     * @param token the token representing the current authentication
     * @param subject the current authenticated subjet
     * @param request the incoming request
     * @param response the outgoing response
     * @throws Exception if there is an error processing the request.
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
                                     ServletResponse response) throws Exception {
        issueSuccessRedirect(request, response);
        return false;
    }
    
    /**
     * If login has failed, redirect user to the error page except if the user is already authenticated, in which case redirect to the
     * default success url.
     * 
     * @param token the token representing the current authentication
     * @param ae the current authentication exception
     * @param request the incoming request
     * @param response the outgoing response
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request,
                                     ServletResponse response) {
        // is user authenticated ?
        Subject subject = getSubject(request, response);
        if (subject.isAuthenticated()) {
            try {
                issueSuccessRedirect(request, response);
            } catch (Exception e) {
                log.error("Cannot redirect to the default success url", e);
            }
        } else {
            try {
                WebUtils.issueRedirect(request, response, failureUrl);
            } catch (IOException e) {
                log.error("Cannot redirect to failure url : {}", failureUrl, e);
            }
        }
        return false;
    }
    
    public void setFailureUrl(String failureUrl) {
        this.failureUrl = failureUrl;
    }
    
    public void setProvider(OAuthProvider provider) {
        providersDefinition = new ProvidersDefinition(provider);
        providersDefinition.init();
    }

	public void setProvidersDefinition(ProvidersDefinition providersDefinition) {
        this.providersDefinition = providersDefinition;
        this.providersDefinition.init();
	}
}
