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

import io.buji.pac4j.session.ShiroSessionStore;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;

import javax.servlet.*;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * Abstract filter containing the configuration and computing the session store.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class AbstractConfigFilter implements Filter {

    private Config config;

    protected SessionStore<J2EContext> retrieveSessionStore() {
        assertNotNull("config", config);
        SessionStore<J2EContext> sessionStore = config.getSessionStore();
        if (sessionStore == null) {
            sessionStore = ShiroSessionStore.INSTANCE;
        }
        return sessionStore;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }
}
