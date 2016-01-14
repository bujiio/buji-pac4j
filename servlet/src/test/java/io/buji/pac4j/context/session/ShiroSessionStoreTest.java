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
package io.buji.pac4j.context.session;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.io.Serializable;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

/**
 * ShiroSessionStore.java test coverage class
 *
 * @author Furkan Yavuz
 * @since 1.4.2
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SecurityUtils.class, ShiroSessionStore.class, Logger.class })
public class ShiroSessionStoreTest {
	
	// Class under test
	private ShiroSessionStore shiroSessionStore;
	private Session sessionMock;
	private Subject subjectMock;
	
	private final String KEY = "KEY";

	@Before
	public void setUp() throws Exception {
		shiroSessionStore = new ShiroSessionStore();
		
		sessionMock = mock(Session.class);
		subjectMock = mock(Subject.class);
		
		doReturn(sessionMock).when(subjectMock).getSession();
		
		mockStatic(SecurityUtils.class);
		when(SecurityUtils.getSubject()).thenReturn(subjectMock);
		
	}

	@Test
	public final void testGetOrCreateSessionId() {
		// given
		Serializable serializableMock = mock(Serializable.class);
		doReturn(serializableMock).when(sessionMock).getId();
		
		//when
		shiroSessionStore.getOrCreateSessionId(null);
		
		// then
		verifyStatic();
		SecurityUtils.getSubject();
		verify(subjectMock).getSession();
		verify(sessionMock).getId();
	}

	@Test
	public final void testGet() {
		// given
		Object objectMock = mock(Object.class);
		doReturn(objectMock).when(sessionMock).getAttribute(KEY);
		
		//when
		shiroSessionStore.get(null, KEY);
		
		// then
		verifyStatic();
		SecurityUtils.getSubject();
		verify(subjectMock).getSession();
		verify(sessionMock).getAttribute(KEY);
	}

	@Test
	public final void testSet() {
		// given
		Object objectMock = mock(Object.class);
		
		//when
		shiroSessionStore.set(null, KEY, objectMock);
		
		// then
		verifyStatic();
		SecurityUtils.getSubject();
		verify(subjectMock).getSession();
		verify(sessionMock).setAttribute(KEY, objectMock);
	}

	@Test
	public final void testSetException() {
		// given
		final String expectedMessage = "Should happen just once at startup in some specific case of Shiro Spring configuration";
		Object objectMock = mock(Object.class);
		UnavailableSecurityManagerException unavailableSecurityManagerExceptionMock = mock(UnavailableSecurityManagerException.class);
		doThrow(unavailableSecurityManagerExceptionMock).when(sessionMock).setAttribute(KEY, objectMock);
		Logger loggerMock = mock(Logger.class);
		setInternalState(ShiroSessionStore.class, "log", loggerMock);
		
		//when
		shiroSessionStore.set(null, KEY, objectMock);
		
		// then
		verifyStatic();
		SecurityUtils.getSubject();
		verify(subjectMock).getSession();
		verify(sessionMock).setAttribute(KEY, objectMock);
		verify(loggerMock).warn(expectedMessage, unavailableSecurityManagerExceptionMock);
	}

}
