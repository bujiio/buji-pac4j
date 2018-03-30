package io.buji.pac4j.custom;

import io.buji.pac4j.context.ShiroSessionStore;
import io.buji.pac4j.filter.LogoutFilter;
import org.apache.shiro.util.StringUtils;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.http.J2ENopHttpActionAdapter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * <p>Title: CustomLogoutFilter </p>
 * <p>Description: CustomLogoutFilter </p>
 * Date: 2018年02月11日 下午2:29 PM
 *
 * @author gaosheng@hiynn.com
 * @version 1.0 </p>
 * Significant Modify：
 * Date               Author           Content
 * ==========================================================
 * 2018年02月11日       SGao        创建文件,实现基本功能
 * <p>
 * ==========================================================
 */
public class CustomLogoutFilter extends LogoutFilter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String preUrl;
        assertNotNull("logoutLogic", this.getLogoutLogic());
        assertNotNull("config", this.getConfig());

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final SessionStore<J2EContext> sessionStore = this.getConfig().getSessionStore();
        final J2EContext context = new J2EContext(request, response, sessionStore != null ? sessionStore : ShiroSessionStore.INSTANCE);

        preUrl = StringUtils.hasText(this.getDefaultUrl()) ? this.getDefaultUrl() : request.getHeader("Referer");

        this.getLogoutLogic().perform(context, this.getConfig(), J2ENopHttpActionAdapter.INSTANCE, preUrl, this.getLogoutUrlPattern(), this.getLocalLogout(), false, this.getCentralLogout());
    }
}
