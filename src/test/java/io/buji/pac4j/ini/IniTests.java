package io.buji.pac4j.ini;

import org.apache.shiro.config.ReflectionBuilder;
import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Jerome LELEU
 * @since 9.0.0
 */
public class IniTests {

    private static final String LOGIN_URL = "http://loginurl";

    @Test
    public void testIniWithoutLombok() {
        final LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("casConfig", "org.pac4j.cas.config.CasConfiguration");
        map.put("casConfig.loginUrl", LOGIN_URL);
        final ReflectionBuilder builder =  new ReflectionBuilder();
        final Map<String, ?> data = builder.buildObjects(map);
        assertNotNull(data);
        final CasConfiguration casConfig = (CasConfiguration) data.get("casConfig");
        assertEquals(LOGIN_URL, casConfig.getLoginUrl());
    }
}