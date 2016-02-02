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

import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * ClientSubjectFactory.java test coverage class
 *
 * @author Furkan Yavuz
 * @since 1.4.2
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ClientSubjectFactory.class, ClientToken.class, SecurityManager.class })
public class ClientSubjectFactoryTest {

	@Test
	public final void testCreateSubject() {
		// given
		ClientSubjectFactory clientSubjectFactory = new ClientSubjectFactory();
		
		ClientToken clientTokenMock = mock(ClientToken.class);
		SecurityManager securityManagerMock = mock(SecurityManager.class);
		WebSubjectContext subjectContextMock = mock(WebSubjectContext.class);
		
		doReturn(true).when(clientTokenMock).isRememberMe();
		doReturn(true).when(subjectContextMock).isAuthenticated();
		
		doReturn(clientTokenMock).when(subjectContextMock).getAuthenticationToken();
		doReturn(securityManagerMock).when(subjectContextMock).resolveSecurityManager();
		
		// when
		clientSubjectFactory.createSubject(subjectContextMock);
		
		// then
		verify(subjectContextMock).setAuthenticated(false);
		
	}

}
