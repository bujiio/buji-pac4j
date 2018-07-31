## buji-pac4j implements dynamic authority

The authority of filter was defied at properties in most project of pac4j,
I need a dynamic authority of request'url which can load data from database, so I changed the
project to achieve the function.

_You should ensure that the login config works well, when you read next content_

#### Inject the authority of the user
* Create a abstract class `CustomAdminAuthGenerator` and extends class `AuthorizationGenerator`
* Override `generate` method
* Create a abstract method `initUserPermissions` and invoke it in `generate`
```
public abstract class CustomAdminAuthGenerator implements AuthorizationGenerator<CommonProfile> {

    @Override
    public CommonProfile generate(final WebContext context, final CommonProfile profile) {
        profile.addPermissions(this.initUserPermissions(profile.getId()));
        profile.clearSensitiveData(); // remove the access token to reduce size and make the remember-me work
        profile.setRemembered(true);
        return profile;
    }
    
    public abstract List<String> initUserPermissions(String credentials);
}
```
#### Inject the authority of the url
* It has the same steps as before
* There has a method `isAnyAccessAllowed` in the follow class, its in order to change _need all_ to _any one_ of the the permissions
```
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

    public abstract List<String> initDynamicPermissions(String requestUri);
}
```

#### Other useful tips
* You can change your callback url to previous instead of a static url, there is example in `io.buji.pac4j.custom.CustomLogoutFilter`
* `Pac4jRealm` can add your own information to `Pac4jSubjectFactory`
* `SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);` can resolve the problem of `@Autowire`


**_you can see the demo [dynamic-pac4j-demo](https://github.com/gao1399677/dynamic-pac4j-demo) for this project_**