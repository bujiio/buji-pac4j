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
package io.buji.pac4j;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This filter retrieves credentials after a user authenticates at the provider to create an ClientToken to finish the authentication
 * process and retrieve the user profile.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public class ClientFilter extends AuthenticatingFilter {
    
    private static Logger log = LoggerFactory.getLogger(ClientFilter.class);
    
    // the url where the application is redirected if the authentication fails
    private String failureUrl;
    
    // the clients definition
    private Clients clients;
    
    // This flag controls the behaviour of the filter after successful redirection
    private boolean redirectAfterSuccessfulAuthentication = true;
    
    /**
     * The token created for this authentication is a ClientToken containing the credentials received after authentication at the provider.
     * These information are received on the callback url (on which the filter must be configured).
     * 
     * @param request the incoming request
     * @param response the outgoing response
     * @throws Exception if there is an error processing the request.
     */
    @Override
    protected AuthenticationToken createToken(final ServletRequest request, final ServletResponse response)
        throws Exception {
        final ShiroWebContext context = new ShiroWebContext(WebUtils.toHttp(request), WebUtils.toHttp(response));
        final Client<Credentials, UserProfile> client = this.clients.findClient(context);
        log.debug("client : {}", client);
        final Credentials credentials = client.getCredentials(context);
        log.debug("credentials : {}", credentials);
        return new ClientToken(client.getName(), credentials);
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
    protected boolean onAccessDenied(final ServletRequest request, final ServletResponse response) throws Exception {
        final AuthenticationToken token;
        try {
            token = createToken(request, response);
        } catch (final RequiresHttpAction e) {
            log.debug("requires HTTP action : {}", e);
            return false;
        }
        try {
            final Subject subject = getSubject(request, response);
            subject.login(token);
            return onLoginSuccess(token, subject, request, response);
        } catch (final NoAuthenticationException e) {
            // no authentication happens but go to the success url however :
            // the protecting filter will have the appropriate behaviour
            return onLoginSuccess(token, null, request, response);
        } catch (final AuthenticationException e) {
            return onLoginFailure(token, e, request, response);
        }
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
    protected boolean isAccessAllowed(final ServletRequest request, final ServletResponse response,
                                      final Object mappedValue) {
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
    protected boolean onLoginSuccess(final AuthenticationToken token, final Subject subject,
                                     final ServletRequest request, final ServletResponse response) throws Exception {
        
        if(false == redirectAfterSuccessfulAuthentication)
            return true;
        else
        {
            issueSuccessRedirect(request, response);
            return false;
        }
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
    protected boolean onLoginFailure(final AuthenticationToken token, final AuthenticationException ae,
                                     final ServletRequest request, final ServletResponse response) {
        // is user authenticated ?
        final Subject subject = getSubject(request, response);
        if (subject.isAuthenticated()) {
            try {
                issueSuccessRedirect(request, response);
            } catch (final Exception e) {
                log.error("Cannot redirect to the default success url", e);
            }
        } else {
            try {
                WebUtils.issueRedirect(request, response, this.failureUrl);
            } catch (final IOException e) {
                log.error("Cannot redirect to failure url : {}", this.failureUrl, e);
            }
        }
        return false;
    }
    
    public String getFailureUrl() {
        return this.failureUrl;
    }
    
    public void setFailureUrl(final String failureUrl) {
        this.failureUrl = failureUrl;
    }
    
    public Clients getClients() {
        return this.clients;
    }
    
    public void setClients(final Clients clients) throws TechnicalException {
        this.clients = clients;
        this.clients.init();
    }

    /**
     * This redirectAfterSuccessfulAuthentication property controls the behaviour of the filter after successful login.
     * If redirection is enabled (default) the filter will redirect the request to original requested url.
     * 
     * In case redirection is disabled the filter will allow the request to passthrough the filter chain. This is useful for cas
     * proxy (proxied application) where the credential receptor url is same as the resource url.
     * 
     * @return current value of the property
     */
    public boolean getRedirectAfterSuccessfulAuthentication()
    {
        return redirectAfterSuccessfulAuthentication;
    }

    public void setRedirectAfterSuccessfulAuthentication(boolean casPassThrough)
    {
        this.redirectAfterSuccessfulAuthentication = casPassThrough;
    }
    
    
}
