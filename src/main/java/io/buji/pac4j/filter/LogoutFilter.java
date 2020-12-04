package io.buji.pac4j.filter;

import io.buji.pac4j.context.ShiroSessionStore;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.JEEContextFactory;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.core.engine.LogoutLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.http.adapter.JEEHttpActionAdapter;
import org.pac4j.core.util.FindBest;

import javax.servlet.*;

import java.io.IOException;

/**
 * <p>This filter handles the (application + identity provider) logout process.</p>
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class LogoutFilter implements Filter {

    private LogoutLogic logoutLogic;

    private Config config;

    private String defaultUrl;

    private String logoutUrlPattern;

    private Boolean localLogout;

    private Boolean centralLogout;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {

        final SessionStore bestSessionStore = FindBest.sessionStore(null, config, ShiroSessionStore.INSTANCE);
        final HttpActionAdapter bestAdapter = FindBest.httpActionAdapter(null, config, JEEHttpActionAdapter.INSTANCE);
        final LogoutLogic bestLogic = FindBest.logoutLogic(logoutLogic, config, DefaultLogoutLogic.INSTANCE);

        final WebContext context = FindBest.webContextFactory(null, config, JEEContextFactory.INSTANCE).newContext(servletRequest, servletResponse, bestSessionStore);

        bestLogic.perform(context, config, bestAdapter, defaultUrl, logoutUrlPattern, localLogout, false, centralLogout);
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

    public LogoutLogic getLogoutLogic() {
        return logoutLogic;
    }

    public void setLogoutLogic(final LogoutLogic logoutLogic) {
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
