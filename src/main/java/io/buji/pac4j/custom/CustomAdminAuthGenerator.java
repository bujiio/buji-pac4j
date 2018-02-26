package io.buji.pac4j.custom;

import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;

public abstract class CustomAdminAuthGenerator implements AuthorizationGenerator<CommonProfile> {

    @Override
    public CommonProfile generate(final WebContext context, final CommonProfile profile) {
//        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        profile.addPermissions(this.initUserPermissions(profile.getId()));
        profile.clearSensitiveData(); // remove the access token to reduce size and make the remember-me work
        profile.setRemembered(true);
        return profile;
    }


    /**
     * <p>Title: initUserPermissions </p>
     * <p>Description: 初始化用户权限 </p>
     * @param credentials 用户身份证信息
     * @return: 当前用户权限
     */
    public abstract List<String> initUserPermissions(String credentials);

}
