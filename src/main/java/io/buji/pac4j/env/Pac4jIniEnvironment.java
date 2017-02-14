package io.buji.pac4j.env;

import org.apache.shiro.config.Ini;
import org.apache.shiro.web.env.IniWebEnvironment;

/**
 * Specific environment ini file for pac4j.
 *
 * @author Jerome Leleu
 * @since 2.2.0
 */
public class Pac4jIniEnvironment extends IniWebEnvironment {

    @Override
    protected Ini getFrameworkIni() {
        return Ini.fromResourcePath("classpath:buji-pac4j-default.ini");
    }
}
