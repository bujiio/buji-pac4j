package io.buji.pac4j.filter;

import io.buji.pac4j.context.ShiroSessionStore;
import io.buji.pac4j.profile.ShiroProfileManager;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.core.engine.LogoutLogic;
import org.pac4j.core.http.adapter.J2ENopHttpActionAdapter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * <p>This filter handles the (application + identity provider) logout process, based on the {@link #logoutLogic}.</p>
 *
 * <p>The configuration can be provided via setter methods: {@link #setConfig(Config)} (security configuration), {@link #setDefaultUrl(String)} (default logourl url),
 * {@link #setLogoutUrlPattern(String)} (pattern that logout urls must match), {@link #setLocalLogout(Boolean)} (whether the application logout must be performed)
 * and {@link #setCentralLogout(Boolean)} (whether the centralLogout must be performed).</p>
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class LogoutFilter implements Filter {

    private LogoutLogic<Object, J2EContext> logoutLogic;

    private Config config;

    private String defaultUrl;

    private String logoutUrlPattern;

    private Boolean localLogout;

    private Boolean centralLogout;

    public LogoutFilter() {
        logoutLogic = new DefaultLogoutLogic<>();
        ((DefaultLogoutLogic<Object, J2EContext>) logoutLogic).setProfileManagerFactory(ShiroProfileManager::new);
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {

        assertNotNull("logoutLogic", logoutLogic);
        assertNotNull("config", config);

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final SessionStore<J2EContext> sessionStore = config.getSessionStore();
        final J2EContext context = new J2EContext(request, response, sessionStore != null ? sessionStore : ShiroSessionStore.INSTANCE);

        logoutLogic.perform(context, config, J2ENopHttpActionAdapter.INSTANCE, this.defaultUrl, this.logoutUrlPattern, this.localLogout, false, this.centralLogout);
    }

    @Override
    public void destroy() {}

    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }

    public String getDefaultUrl() {
        return this.defaultUrl;
    }

    public void setDefaultUrl(final String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public String getLogoutUrlPattern() {
        return logoutUrlPattern;
    }

    public void setLogoutUrlPattern(String logoutUrlPattern) {
        this.logoutUrlPattern = logoutUrlPattern;
    }

    public LogoutLogic<Object, J2EContext> getLogoutLogic() {
        return logoutLogic;
    }

    public void setLogoutLogic(final LogoutLogic<Object, J2EContext> logoutLogic) {
        this.logoutLogic = logoutLogic;
    }

    public Boolean getLocalLogout() {
        return localLogout;
    }

    public void setLocalLogout(final Boolean localLogout) {
        this.localLogout = localLogout;
    }

    public Boolean getCentralLogout() {
        return centralLogout;
    }

    public void setCentralLogout(final Boolean centralLogout) {
        this.centralLogout = centralLogout;
    }
}
