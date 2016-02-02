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
import static org.powermock.api.mockito.PowerMockito.mock;

import org.junit.Test;
import org.pac4j.core.credentials.Credentials;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

/**
 * ClientToken.java test coverage class
 *
 * @author Furkan Yavuz
 * @since 1.4.2
 */
public class ClientTokenTest {
	
	private final String CLIENT_NAME = "CLIENT_NAME";

	@Test
	public final void testConstructor() {
		Credentials credentialsMock = mock(Credentials.class);
		
		ClientToken clientToken = new ClientToken(CLIENT_NAME, credentialsMock);
		
		assertEquals("Constructor must set clientName field to " + CLIENT_NAME, CLIENT_NAME, clientToken.getClientName());
		assertEquals("Constructor must set credentials field to " + credentialsMock, credentialsMock, clientToken.getCredentials());
			
	}

	@Test
	public final void testGetterSetter() throws Exception {
		Validator validator = ValidatorBuilder.create().with(new SetterTester()).with(new GetterTester()).build();
		validator.validate(PojoClassFactory.getPojoClass(ClientToken.class));
	}
}
