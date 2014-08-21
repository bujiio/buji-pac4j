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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.pac4j.core.context.J2EContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation leverages the J2E context with Shiro session.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class ShiroWebContext extends J2EContext {
    
    protected static Logger log = LoggerFactory.getLogger(ShiroWebContext.class);
    
    public ShiroWebContext() {
        super(null, null);
    }
    
    public ShiroWebContext(final HttpServletRequest request, final HttpServletResponse response) {
        super(request, response);
    }
    
    @Override
    public void setSessionAttribute(final String name, final Object value) {
        try {
            SecurityUtils.getSubject().getSession().setAttribute(name, value);
        } catch (final UnavailableSecurityManagerException e) {
            log.warn("Should happen just once at startup in some specific case of Shiro Spring configuration", e);
        }
    }
    
    @Override
    public Object getSessionAttribute(final String name) {
        return SecurityUtils.getSubject().getSession().getAttribute(name);
    }
}
