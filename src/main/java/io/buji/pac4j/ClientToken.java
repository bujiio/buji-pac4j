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

import org.apache.shiro.authc.AuthenticationToken;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.CommonHelper;

/**
 * This class represents a token for an authentication process (client name + credentials + user identifier after authentication).
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class ClientToken implements AuthenticationToken {
    
    private static final long serialVersionUID = 3141878022445836151L;
    
    private final String clientName;
    
    private final Credentials credentials;
    
    private String userId;
    
    public ClientToken(final String clientName, final Credentials credentials) {
        this.clientName = clientName;
        this.credentials = credentials;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    public String getClientName() {
        return this.clientName;
    }
    
    public Object getCredentials() {
        return this.credentials;
    }
    
    public Object getPrincipal() {
        return this.userId;
    }
    
    @Override
    public String toString() {
        return CommonHelper.toString(ClientToken.class, "clientName", this.clientName, "credentials", this.credentials,
                                     "userId", this.userId);
    }
}
