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

import java.util.Map;

import org.scribe.model.Token;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.BaseOAuthProvider;
import org.scribe.up.session.UserSession;

/**
 * This provider is a mock with specific behaviours for tests.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class MockOAuthProvider extends BaseOAuthProvider {
    
    public final static String TYPE = "mockType";
    
    public final static String NULL_USER_PROFILE = "nullUserProfile";
    
    public final static String USER_PROFILE_WITH_EMPTY_ID = "userProfileWithEmptyId";
    
    public final static String USER_PROFILE_WITHOUT_ATTRIBUTE = "userProfileWithoutAttribute";
    
    public final static String USER_PROFILE_WITH_ONE_ATTRIBUTE = "userProfileWithOneAttribute";
    
    public final static String ATTRIBUTE_KEY = "attributeKey";
    
    public final static String ATTRIBUTE_VALUE = "attributeValue";
    
    public String getType() {
        return TYPE;
    }
    
    public String getAuthorizationUrl(UserSession userSession) {
        return null;
    }
    
    public UserProfile getUserProfile(OAuthCredential credential) {
        String token = credential.getToken();
        if (NULL_USER_PROFILE.equals(token)) {
            return null;
        } else if (USER_PROFILE_WITH_EMPTY_ID.equals(token)) {
            return new MockUserProfile();
        } else if (USER_PROFILE_WITHOUT_ATTRIBUTE.equals(token)) {
            return new MockUserProfile(token);
        } else if (USER_PROFILE_WITH_ONE_ATTRIBUTE.equals(token)) {
            MockUserProfile userProfile = new MockUserProfile(token);
            userProfile.addAttribute(ATTRIBUTE_KEY, ATTRIBUTE_VALUE);
            return userProfile;
        }
        throw new IllegalStateException();
    }
    
    public OAuthCredential getCredential(UserSession userSession, Map<String, String[]> parameters) {
        return null;
    }

	@Override
	protected BaseOAuthProvider newProvider() {
		return null;
	}

	@Override
	protected void internalInit() {
	}

	@Override
	protected Token getAccessToken(OAuthCredential credential) {
		return null;
	}

	@Override
	protected String getProfileUrl() {
		return null;
	}

	@Override
	protected UserProfile extractUserProfile(String body) {
		return null;
	}

	@Override
	protected OAuthCredential extractCredentialFromParameters(
			UserSession session, Map<String, String[]> parameters) {
		return null;
	}

	@Override
	public String getCallbackUrl() {
        return "http://someCallbackUrl";
    }
}
