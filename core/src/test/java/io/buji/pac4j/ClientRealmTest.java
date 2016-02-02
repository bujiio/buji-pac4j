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
package io.buji.pac4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.CommonProfile;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * ClientRealm.java test coverage class
 *
 * @author Furkan Yavuz
 * @since 1.4.2
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { ClientToken.class, ClientRealm.class })
public class ClientRealmTest {
	
	private ClientRealm clientRealm;
	
	@SuppressWarnings("rawtypes")
	private Client clientMock;
	private Clients clientsMock;
	private ClientToken clientTokenMock;
	private Credentials credentialsMock;
	private CommonProfile commonProfileMock;
	private SimpleAuthorizationInfo simpleAuthorizationInfoMock;
	private SimpleAuthenticationInfo simpleAuthenticationInfoMock;
	private SimplePrincipalCollection simplePrincipalCollectionMock;
	private Collection<CommonProfile> profilesMock;
	
	private final String TEST_USER_ID = "TEST_USER_ID";
	private final String TEST_CLIENT_NAME = "TEST_CLIENT_NAME";
	private final String DEFAULT_ROLES = "READ, WRITE";
	private final String DEFAULT_PERMISSIONS = "READ, WRITE";

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		clientRealm = new ClientRealm();
		
		clientMock = mock(Client.class);
		clientsMock = mock(Clients.class);
		clientTokenMock = mock(ClientToken.class);
		credentialsMock = mock(Credentials.class);
		commonProfileMock = mock(CommonProfile.class);
		simpleAuthorizationInfoMock = mock(SimpleAuthorizationInfo.class);
		simplePrincipalCollectionMock = mock(SimplePrincipalCollection.class);
		simpleAuthenticationInfoMock = mock(SimpleAuthenticationInfo.class);
		
		profilesMock = new ArrayList<CommonProfile>();
		profilesMock.add(commonProfileMock);
		
		clientRealm.setClients(clientsMock);
		clientRealm.setDefaultRoles(DEFAULT_ROLES);
		clientRealm.setDefaultPermissions(DEFAULT_PERMISSIONS);
		
		doReturn(clientMock).when(clientsMock).findClient(TEST_CLIENT_NAME);
		doReturn(credentialsMock).when(clientTokenMock).getCredentials();
		doReturn(TEST_CLIENT_NAME).when(clientTokenMock).getClientName();
		doReturn(TEST_USER_ID).when(commonProfileMock).getTypedId();
		doReturn(commonProfileMock).when(clientMock).getUserProfile(credentialsMock, null);
		doReturn(profilesMock).when(simplePrincipalCollectionMock).byType(CommonProfile.class);
		
		whenNew(SimplePrincipalCollection.class).withAnyArguments().thenReturn(simplePrincipalCollectionMock);
		whenNew(SimpleAuthorizationInfo.class).withNoArguments().thenReturn(simpleAuthorizationInfoMock);
		whenNew(SimpleAuthenticationInfo.class).withArguments(simplePrincipalCollectionMock, credentialsMock).thenReturn(simpleAuthenticationInfoMock);
	}

	@Test
	public final void testDoGetAuthenticationInfo() {
		// when
		AuthenticationInfo simpleAuthenticationInfo = clientRealm.doGetAuthenticationInfo(clientTokenMock);
		
		// then
		assertEquals("SimpleAuthenticationInfo must be created with simplePrincipalCollectionMock and credentialsMock arguments", simpleAuthenticationInfoMock, simpleAuthenticationInfo);
	}

	@Test
	public final void testDoGetAuthorizationInfo() {
		// when
		AuthorizationInfo simpleAuthorizationInfo = clientRealm.doGetAuthorizationInfo(simplePrincipalCollectionMock);
		
		// then
		assertEquals("SimpleAuthorizationInfo must be equal to simpleAuthorizationInfoMock", simpleAuthorizationInfoMock, simpleAuthorizationInfo);
	}

	@Test
	public final void testSplit() {
		// when
		List<String> list = clientRealm.split(DEFAULT_ROLES);
		
		// then
		assertTrue("List must have READ, WRITE values.", list.contains("READ") && list.contains("WRITE"));
	}

}
