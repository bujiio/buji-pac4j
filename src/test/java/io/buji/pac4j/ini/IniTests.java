package io.buji.pac4j.ini;

import lombok.val;
import org.apache.shiro.config.ReflectionBuilder;
import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Jerome LELEU
 * @since 9.0.0
 */
public class IniTests {

    private static final String LOGIN_URL = "http://loginurl";

    @Test
    public void testIniWithLombok() {
        val map = new LinkedHashMap<String, String>();
        map.put("casConfig", "org.pac4j.cas.config.CasConfiguration");
        map.put("casConfig.loginUrl", LOGIN_URL);
        val builder =  new ReflectionBuilder();
        val data = builder.buildObjects(map);
        assertNotNull(data);
        val casConfig = (CasConfiguration) data.get("casConfig");
        assertEquals(LOGIN_URL, casConfig.getLoginUrl());
    }
}
