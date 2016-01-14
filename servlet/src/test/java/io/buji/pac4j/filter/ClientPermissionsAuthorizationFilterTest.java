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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.Whitebox.getInternalState;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.J2EContext;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * ClientPermissionsAuthorizationFilter.java test coverage class
 *
 * @author Furkan Yavuz
 * @since 1.4.2
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ClientPermissionsAuthorizationFilter.class, IndirectClient.class })
public class ClientPermissionsAuthorizationFilterTest {
	
	private ClientPermissionsAuthorizationFilter clientPermissionsAuthorizationFilter;

	@Before
	public void setUp() throws Exception {
		clientPermissionsAuthorizationFilter = new ClientPermissionsAuthorizationFilter();
	}

	@Test
	public final void testIsLoginRequest() {
		// when
		boolean result = clientPermissionsAuthorizationFilter.isLoginRequest(null, null);
		
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
		clientPermissionsAuthorizationFilter.setClient(indirectClientMock);
		
		// when
		clientPermissionsAuthorizationFilter.redirectToLogin(null, null);
		
		// then
		verify(indirectClientMock).redirect(j2EContextMock, true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public final void testSetClient() {
		// given
		IndirectClient indirectClientMock = mock(IndirectClient.class);
		
		// when
		clientPermissionsAuthorizationFilter.setClient(indirectClientMock);
		
		// then
		assertEquals("Client must be set to " + indirectClientMock, indirectClientMock, getInternalState(clientPermissionsAuthorizationFilter, "client"));
	}

}
