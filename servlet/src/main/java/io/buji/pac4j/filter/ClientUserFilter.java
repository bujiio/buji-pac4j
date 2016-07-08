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
package io.buji.pac4j.filter;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;

import io.buji.pac4j.context.session.ShiroSessionStore;

/**
 * This class specializes the UserFilter to have a login url which is the redirection url to the provider.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class ClientUserFilter extends UserFilter {

    private IndirectClient<Credentials, CommonProfile> client;

    @Override
    protected boolean isLoginRequest(final ServletRequest request, final ServletResponse response) {
        return false;
    }

    @Override
    protected void redirectToLogin(final ServletRequest request, final ServletResponse response) throws IOException {
        final J2EContext context = new J2EContext(WebUtils.toHttp(request), WebUtils.toHttp(response), new ShiroSessionStore<J2EContext>());
        try {
            this.client.redirect(context);
        } catch (HttpAction e) {
        }
    }

    public void setClient(final IndirectClient<Credentials, CommonProfile> client) {
        this.client = client;
    }
}
