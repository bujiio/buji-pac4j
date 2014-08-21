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

import org.apache.shiro.authc.AuthenticationException;

/**
 * This exception is thrown when no authentication has been achieved (cancelled at the provider or just check for authentication).
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class NoAuthenticationException extends AuthenticationException {
    
    private static final long serialVersionUID = 2642292107933837159L;
    
    public NoAuthenticationException(final String message) {
        super(message);
    }
}
