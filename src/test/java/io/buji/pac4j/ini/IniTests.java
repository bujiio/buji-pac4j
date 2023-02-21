package io.buji.pac4j.ini;

import org.apache.commons.beanutils.FluentPropertyBeanIntrospector;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;

import java.beans.PropertyDescriptor;

import static org.junit.Assert.assertNotNull;

/**
 * @author Jerome LELEU
 * @since 9.0.0
 */
public class IniTests {

    @Test
    public void testIniWithLombok() throws Exception {
        /*final LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("casConfig", "org.pac4j.cas.config.CasConfiguration");
        map.put("casConfig.loginUrl", LOGIN_URL);
        final ReflectionBuilder builder =  new ReflectionBuilder();
        final Map<String, ?> data = builder.buildObjects(map);
        assertNotNull(data);
        final CasConfiguration casConfig = (CasConfiguration) data.get("casConfig");
        assertEquals(LOGIN_URL, casConfig.getLoginUrl());*/
        final CasConfiguration casConfig = new CasConfiguration();
        final PropertyUtilsBean prop = new PropertyUtilsBean();
        prop.addBeanIntrospector(new FluentPropertyBeanIntrospector());
        final PropertyDescriptor descriptor = prop.getPropertyDescriptor(casConfig, "loginUrl");
        assertNotNull(descriptor.getWriteMethod());
    }
}
