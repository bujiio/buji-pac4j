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

import junit.framework.TestCase;

import org.scribe.up.credential.OAuthCredential;

/**
 * This class tests the {@link OAuthToken}.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuthTokenTest extends TestCase {
    
    private static final String USER_ID = "userId";
    
    public void testToken() {
        OAuthCredential credential = new OAuthCredential(null, null, null, null);
        OAuthToken oauthToken = new OAuthToken(credential);
        assertEquals(null, oauthToken.getPrincipal());
        oauthToken.setUserId(USER_ID);
        assertEquals(USER_ID, (String) oauthToken.getPrincipal());
        assertEquals(credential, oauthToken.getCredentials());
    }
}
