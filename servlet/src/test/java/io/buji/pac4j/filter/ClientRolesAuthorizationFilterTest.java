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

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.Whitebox.getInternalState;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.CommonProfile;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * ClientRolesAuthorizationFilter.java test coverage class
 *
 * @author Furkan Yavuz
 * @since 1.4.2
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ClientRolesAuthorizationFilter.class, IndirectClient.class })
public class ClientRolesAuthorizationFilterTest {
	
	private ClientRolesAuthorizationFilter clientRolesAuthorizationFilter;

	@Before
	public void setUp() throws Exception {
		clientRolesAuthorizationFilter = new ClientRolesAuthorizationFilter();
	}

	@Test
	public final void testIsLoginRequest() {
		// when
		boolean result = clientRolesAuthorizationFilter.isLoginRequest(null, null);
		
		// then
		assertFalse("Logically, result must be false", result);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public final void testRedirectToLogin() throws Exception {
		// given
		J2EContext j2EContextMock  = mock(J2EContext.class);
		whenNew(J2EContext.class).withAnyArguments().thenReturn(j2EContextMock);
		IndirectClient indirectClientMock = mock(IndirectClient.class);
		clientRolesAuthorizationFilter.setClient(indirectClientMock);
		
		// when
		clientRolesAuthorizationFilter.redirectToLogin(null, null);
		
		// then
		verify(indirectClientMock).redirect(j2EContextMock, true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public final void testSetClient() {
		// given
		IndirectClient indirectClientMock = mock(IndirectClient.class);
		
		// when
		clientRolesAuthorizationFilter.setClient(indirectClientMock);
		
		// then
		assertEquals("Client must be set to " + indirectClientMock, indirectClientMock, getInternalState(clientRolesAuthorizationFilter, "client"));
	}

	/**
	 * Test for setClient with subclasses of {@link IndirectClient}, e.g. CasClient.
	 */
	@Test
	public final void testSetClientWithExtends() {
		// given
		IndirectClient<? extends Credentials, ? extends CommonProfile> indirectClientMock = mock(IndirectClient.class);

		// when
		clientRolesAuthorizationFilter.setClient(indirectClientMock);

		// then
		assertEquals("Client must be set to " + indirectClientMock, indirectClientMock, getInternalState(clientRolesAuthorizationFilter, "client"));
	}

}
