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

import org.apache.shiro.SecurityUtils;
import org.scribe.up.session.UserSession;

/**
 * This implementation uses the Shiro session for the user session.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class ShiroUserSession implements UserSession {

    public void setAttribute(String key, Object value) {
        SecurityUtils.getSubject().getSession().setAttribute(key, value);
    }

    public Object getAttribute(String key) {
        return SecurityUtils.getSubject().getSession().getAttribute(key);
    }
}
