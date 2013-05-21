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
package io.buji.pac4j.filter;

import io.buji.pac4j.ShiroWebContext;

import java.io.IOException;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.util.WebUtils;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

/**
 * This filter helper providers common behaviour to {@link ClientPermissionsAuthorizationFilter}, {@link ClientRolesAuthorizationFilter} and
 * {@clientUserFIlter}.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class FilterHelper {
    
    private final static ShiroWebContext shiroWebContext = new ShiroWebContext();
    
    private final static String ATTEMPTED_AUTHENTICATION_SUFFIX = "#" + FilterHelper.class.getName();
    
    public static String getLoginUrl(final BaseClient<Credentials, CommonProfile> client,
                                     final HttpServletRequest request, final HttpServletResponse response) {
        return client.getRedirectionUrl(new ShiroWebContext(request, response), true);
    }
    
    public static boolean hasProcessedAlready(final BaseClient<Credentials, CommonProfile> client,
                                              final ServletResponse response) throws IOException {
        final String startAuth = getValue(client);
        if (CommonHelper.isNotBlank(startAuth)) {
            cleanValue(client);
            WebUtils.toHttp(response).sendError(HttpServletResponse.SC_FORBIDDEN);
            return true;
        }
        return false;
    }
    
    protected static void cleanValue(final BaseClient<Credentials, CommonProfile> client) {
        setValue(client, null);
    }
    
    protected static String getValue(final BaseClient<Credentials, CommonProfile> client) {
        return (String) shiroWebContext.getSessionAttribute(client.getName() + ATTEMPTED_AUTHENTICATION_SUFFIX);
    }
    
    public static void setValue(final BaseClient<Credentials, CommonProfile> client, final String value) {
        shiroWebContext.setSessionAttribute(client.getName() + ATTEMPTED_AUTHENTICATION_SUFFIX, value);
    }
}
