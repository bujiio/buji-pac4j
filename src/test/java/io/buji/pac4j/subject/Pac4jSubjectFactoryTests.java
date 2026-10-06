package io.buji.pac4j.subject;

import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.token.Pac4jToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DisabledSessionException;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.subject.WebSubject;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/** Tests the web and non-web subject paths without replacing the caller's subject after login. */
public final class Pac4jSubjectFactoryTests {

    @Test
    public void testDisabledSessionCreationIsRespected() {
        final DefaultSecurityManager manager = new DefaultSecurityManager();
        manager.setSubjectFactory(new Pac4jSubjectFactory());
        try {
            final Subject subject = new Subject.Builder(manager).sessionCreationEnabled(false).buildSubject();
            assertThrows(DisabledSessionException.class, subject::getSession);
        } finally {
            manager.destroy();
        }
    }

    @Test
    public void testWebRememberMeDoesNotBecomeFullyAuthenticated() {
        final DefaultWebSecurityManager manager = new DefaultWebSecurityManager(new Pac4jRealm());
        manager.setSubjectFactory(new Pac4jSubjectFactory());
        manager.setRememberMeManager(null);
        final DefaultSessionStorageEvaluator evaluator = new DefaultSessionStorageEvaluator();
        evaluator.setSessionStorageEnabled(false);
        ((DefaultSubjectDAO) manager.getSubjectDAO()).setSessionStorageEvaluator(evaluator);
        final HttpServletRequest request = servletProxy(HttpServletRequest.class);
        final HttpServletResponse response = servletProxy(HttpServletResponse.class);
        try {
            final WebSubject.Builder builder = new WebSubject.Builder(manager, request, response);
            builder.sessionCreationEnabled(false);
            final WebSubject subject = builder.buildWebSubject();
            final CommonProfile profile = new CommonProfile();
            profile.setId("user");
            profile.setRemembered(true);
            subject.login(new Pac4jToken(List.of(profile), true));
            assertFalse(subject.isAuthenticated());
            assertTrue(subject.isRemembered());
            assertSame(request, subject.getServletRequest());
            assertSame(response, subject.getServletResponse());
            assertThrows(DisabledSessionException.class, subject::getSession);
        } finally {
            manager.destroy();
        }
    }

    private static <T> T servletProxy(final Class<T> type) {
        final Map<String, Object> attributes = new HashMap<>();
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, (proxy, method, args) -> {
            if ("getAttribute".equals(method.getName())) {
                return attributes.get(args[0]);
            } else if ("setAttribute".equals(method.getName())) {
                attributes.put((String) args[0], args[1]);
            } else if (method.getReturnType() == boolean.class) {
                return false;
            } else if (method.getReturnType() == int.class) {
                return 0;
            }
            return null;
        }));
    }
}
