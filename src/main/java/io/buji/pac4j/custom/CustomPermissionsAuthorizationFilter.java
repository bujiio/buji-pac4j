package io.buji.pac4j.custom;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Title: CustomPermissionsAuthorizationFilter </p>
 * <p>Description: CustomPermissionsAuthorizationFilter </p>
 * Date: 2018年02月09日 下午11:03 AM
 *
 * @author gaosheng@hiynn.com
 * @version 1.0 </p>
 * Significant Modify：
 * Date               Author           Content
 * ==========================================================
 * 2018年02月09日       SGao        创建文件,实现基本功能
 * <p>
 * ==========================================================
 */
public abstract class CustomPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter {


    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        // use your services to load permissions
        HttpServletRequest req = (HttpServletRequest) request;
        List<String> iniPermissions = new ArrayList<>(Arrays.asList((String[]) mappedValue));

        iniPermissions.addAll(this.initDynamicPermissions(req.getRequestURI()));
        String[] perms = iniPermissions.toArray(new String[0]);

        return this.isAnyAccessAllowed(request, response, perms);
    }


    /**
     * <p>Title: isAnyAccessAllowed </p>
     * <p>Description: change permission authentication </p>
     * @param request
     * @param response
     * @param mappedValue
     * @return:
     */
    public boolean isAnyAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        Subject subject = this.getSubject(request, response);
        String[] perms = (String[]) mappedValue;
        boolean isPermitted = false;
        if (perms != null && perms.length > 0) {
            for (String perm : perms) {
                if (subject.isPermitted(perm)) {
                    isPermitted = true;
                    break;
                }
            }
        } else {
            isPermitted = true;
        }

        return isPermitted;
    }

    /**
     * <p>Title: initDynamicPermissions </p>
     * <p>Description: 根据当前请求路径获取路径所需权限 </p>
     *
     * @param requestUri 当前请求路径
     * @return: 路径所需权限
     */
    public abstract List<String> initDynamicPermissions(String requestUri);
}
