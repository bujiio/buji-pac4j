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

import io.buji.pac4j.ShiroWebContext;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;

/**
 * This class specializes the RolesAuthorizationFilter to have a login url which is the redirection url to the provider.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class ClientRolesAuthorizationFilter extends RolesAuthorizationFilter {

    protected Clients clients;

    protected String clientName;

    @Override
    protected boolean isLoginRequest(final ServletRequest request, final ServletResponse response) {
        return false;
    }

    @Override
    protected void redirectToLogin(final ServletRequest request, final ServletResponse response) throws IOException {
        final WebContext context = new ShiroWebContext(WebUtils.toHttp(request), WebUtils.toHttp(response));
        final Client client = clients.findClient(context, clientName);
        try {
            client.redirect(context, true);
        } catch (RequiresHttpAction e) {
        }
    }

    public Clients getClients() {
        return clients;
    }

    public void setClients(Clients clients) {
        this.clients = clients;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }
}
