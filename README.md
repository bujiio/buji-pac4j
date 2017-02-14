<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-shiro.png" width="300" />
</p>

The `buji-pac4j` project is an **easy and powerful security library for Shiro** web applications which supports authentication and authorization, but also advanced features like CSRF protection.
It's based on Java 8, Shiro 1.4 and on the **[pac4j security engine](https://github.com/pac4j/pac4j)**. It's available under the Apache 2 license.

[**Main concepts and components:**](http://www.pac4j.org/docs/main-concepts-and-components.html)

1) A [**client**](http://www.pac4j.org/docs/clients.html) represents an authentication mechanism. It performs the login process and returns a user profile. An indirect client is for UI authentication while a direct client is for web services authentication:

&#9656; OAuth - SAML - CAS - OpenID Connect - HTTP - OpenID - Google App Engine - LDAP - SQL - JWT - MongoDB - Stormpath - IP address

2) An [**authorizer**](http://www.pac4j.org/docs/authorizers.html) is meant to check authorizations on the authenticated user profile(s) or on the current web context:

&#9656; Roles / permissions - Anonymous / remember-me / (fully) authenticated - Profile type, attribute -  CORS - CSRF - Security headers - IP address, HTTP method

3) The `SecurityFilter` protects an url by checking that the user is authenticated and that the authorizations are valid, according to the clients and authorizers configuration. If the user is not authenticated, it performs authentication for direct clients or starts the login process for indirect clients

4) The `CallbackFilter` finishes the login process for an indirect client.

==

Just follow these easy steps to secure your Shiro web application:

### 1) Add the required dependencies (`buji-pac4j` + `pac4j-*` libraries)

You need to add a dependency on:
 
- the `buji-pac4j` library (<em>groupId</em>: **io.buji**, *version*: **2.2.0-SNAPSHOT**)
- the appropriate `pac4j` [submodules](http://www.pac4j.org/docs/clients.html) (<em>groupId</em>: **org.pac4j**, *version*: **1.9.6**): `pac4j-oauth` for OAuth support (Facebook, Twitter...), `pac4j-cas` for CAS support, `pac4j-ldap` for LDAP authentication, etc.

All released artifacts are available in the [Maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cpac4j).

---

### 2) Define the configuration (`Config` + `Client` + `Authorizer`)

The configuration (`org.pac4j.core.config.Config`) contains all the clients and authorizers required by the application to handle security.

It must be defined in your `shiro.ini` file:

```properties
[main]
roleAdminAuthGenerator = org.pac4j.demo.shiro.RoleAdminAuthGenerator

googleOidClient = org.pac4j.oidc.client.GoogleOidcClient
googleOidClient.clientID = googleClientID
googleOidClient.secret = googleSecret
googleOidClient.useNonce = true
googleOidClient.authorizationGenerator = $roleAdminAuthGenerator

saml2Config = org.pac4j.saml.client.SAML2ClientConfiguration
saml2Config.keystorePath = resource:samlKeystore.jks
saml2Config.keystorePassword = pac4j-demo-passwd
saml2Config.privateKeyPassword = pac4j-demo-passwd
saml2Config.identityProviderMetadataPath = resource:metadata-okta.xml
saml2Config.maximumAuthenticationLifetime = 3600
saml2Config.serviceProviderEntityId = http://localhost:8080/callback?client_name=SAML2Client
saml2Config.serviceProviderMetadataPath = sp-metadata.xml

saml2Client = org.pac4j.saml.client.SAML2Client
saml2Client.configuration = $saml2Config

facebookClient = org.pac4j.oauth.client.FacebookClient
facebookClient.key = fbkey
facebookClient.secret = fbSecret

twitterClient = org.pac4j.oauth.client.TwitterClient
twitterClient.key = twKey
twitterClient.secret = twSecret

simpleAuthenticator = org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator

formClient = org.pac4j.http.client.indirect.FormClient
formClient.loginUrl = http://localhost:8080/loginForm.jsp
formClient.authenticator = $simpleAuthenticator

indirectBasicAuthClient = org.pac4j.http.client.indirect.IndirectBasicAuthClient
indirectBasicAuthClient.authenticator = $simpleAuthenticator

casClient = org.pac4j.cas.client.CasClient
casClient.casLoginUrl = https://casserverpac4j.herokuapp.com

jwtAuthenticator = org.pac4j.jwt.credentials.authenticator.JwtAuthenticator
jwtAuthenticator.signingSecret = signingSecret
jwtAuthenticator.encryptionSecret = encryptionSecret

parameterClient = org.pac4j.http.client.direct.ParameterClient
parameterClient.parameterName = token
parameterClient.authenticator = $jwtAuthenticator

directBasicAuthClient = org.pac4j.http.client.direct.DirectBasicAuthClient
directBasicAuthClient.authenticator = $simpleAuthenticator

clients.callbackUrl = http://localhost:8080/callback
clients.clients = $googleOidClient,$facebookClient,$twitterClient,$formClient,$indirectBasicAuthClient,$casClient,$saml2Client,$parameterClient,$directBasicAuthClient

requireRoleAdmin = org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer
requireRoleAdmin.elements = ROLE_ADMIN

customAuthorizer = org.pac4j.demo.shiro.CustomAuthorizer

excludedPathMatcher = org.pac4j.core.matching.ExcludedPathMatcher
excludedPathMatcher.excludePath = ^/facebook/notprotected\.jsp$

config.authorizers = admin:$requireRoleAdmin,custom:$customAuthorizer
config.matchers = excludedPath:$excludedPathMatcher
```

The `clients` and `config` components are already available using the `Pac4jIniEnvironment`.

`http://localhost:8080/callback` is the url of the callback endpoint, which is only necessary for indirect clients.

Notice that you can define specific [matchers](http://www.pac4j.org/docs/matchers.html) via the `matchers` map.

---

### 3) Protect urls (`SecurityFilter`)

You can protect (authentication + authorizations) the urls of your Shiro application by using the `SecurityFilter` and declaring the filter on the appropriate url mapping. It has the following behaviour:

1) If the HTTP request matches the `matchers` configuration (or no `matchers` are defined), the security is applied. Otherwise, the user is automatically granted access.

2) First, if the user is not authenticated (no profile) and if some clients have been defined in the `clients` parameter, a login is tried for the direct clients.

3) Then, if the user has a profile, authorizations are checked according to the `authorizers` configuration. If the authorizations are valid, the user is granted access. Otherwise, a 403 error page is displayed.

4) Finally, if the user is still not authenticated (no profile), he is redirected to the appropriate identity provider if the first defined client is an indirect one in the `clients` configuration. Otherwise, a 401 error page is displayed.


The following parameters are available:

1) `config`: the security configuration previously defined

2) `clients` (optional): the list of client names (separated by commas) used for authentication:
- in all cases, this filter requires the user to be authenticated. Thus, if the `clients` is blank or not defined, the user must have been previously authenticated
- if the `client_name` request parameter is provided, only this client (if it exists in the `clients`) is selected.

3) `authorizers` (optional): the list of authorizer names (separated by commas) used to check authorizations:
- if the `authorizers` is blank or not defined, no authorization is checked
- the following authorizers are available by default (without defining them in the configuration):
  * `isFullyAuthenticated` to check if the user is authenticated but not remembered, `isRemembered` for a remembered user, `isAnonymous` to ensure the user is not authenticated, `isAuthenticated` to ensure the user is authenticated (not necessary by default unless you use the `AnonymousClient`)
  * `hsts` to use the `StrictTransportSecurityHeader` authorizer, `nosniff` for `XContentTypeOptionsHeader`, `noframe` for `XFrameOptionsHeader `, `xssprotection` for `XSSProtectionHeader `, `nocache` for `CacheControlHeader ` or `securityHeaders` for the five previous authorizers
  * `csrfToken` to use the `CsrfTokenGeneratorAuthorizer` with the `DefaultCsrfTokenGenerator` (it generates a CSRF token and saves it as the `pac4jCsrfToken` request attribute and in the `pac4jCsrfToken` cookie), `csrfCheck` to check that this previous token has been sent as the `pac4jCsrfToken` header or parameter in a POST request and `csrf` to use both previous authorizers.

4) `matchers` (optional): the list of matcher names (separated by commas) that the request must satisfy to check authentication / authorizations

5) `multiProfile` (optional): it indicates whether multiple authentications (and thus multiple profiles) must be kept at the same time (`false` by default).

In your `shiro.ini` file:

```properties
[main]
facebookSecurityFilter = io.buji.pac4j.filter.SecurityFilter
facebookSecurityFilter.config = $config
facebookSecurityFilter.clients = FacebookClient

[url]
/facebook/** = facebookSecurityFilter
```

---

### 4) Define the callback endpoint only for indirect clients (`CallbackFilter`)

For indirect clients (like Facebook), the user is redirected to an external identity provider for login and then back to the application.
Thus, a callback endpoint is required in the application. It is managed by the `CallbackFilter` which has the following behaviour:

1) the credentials are extracted from the current request to fetch the user profile (from the identity provider) which is then saved in the web session

2) finally, the user is redirected back to the originally requested url (or to the `defaultUrl`).


The following parameters are available:

1) `config`: the security configuration previously defined

2) `defaultUrl` (optional): it's the default url after login if no url was originally requested (`/` by default)

3) `multiProfile` (optional): it indicates whether multiple authentications (and thus multiple profiles) must be kept at the same time (`false` by default).

In your `shiro.ini` file:

```properties
[url]
/callback = callbackFilter
```

The `callbackFilter` component is already available using the `Pac4jIniEnvironment`.

---

### 5) Get the user profile

Like for any Shiro web application, you can get the authenticated user via the `SecurityUtils.getSubject().getPrincipals()`.
If the user is authenticated or remembered, the appropriate principal: `Pac4jPrincipal` will be stored in the principals,
on which you can get the main profile (`getProfile` method) or all profiles (`getProfiles` method) of the authenticated user: 

```java
final PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
if (principals != null) {
    final Pac4jPrincipal principal = principals.oneByType(Pac4jPrincipal.class);
    if (principal != null) {
        CommonProfile profile = principal.getProfile();
    }
}
```

The retrieved profile is at least a `CommonProfile`, from which you can retrieve the most common attributes that all profiles share. But you can also cast the user profile to the appropriate profile according to the provider used for authentication. For example, after a Facebook authentication:

```java
FacebookProfile facebookProfile = (FacebookProfile) commonProfile;
```

---

### 6) Logout

Like for any Shiro webapp, use the default logout filter (in your `shiro.ini` file):

```properties
[url]
/logout = logout
```

---

## Migration guide

### From the deprecated shiro-cas module (CAS support)

Instead of using the `shiro-cas` module, you need to use the `buji-pac4j` library and the `pac4j-cas` module. Though, the way both implementations work is close.

The `CasFilter` is replaced by the `CallbackFilter` which has the same role (receiving callbacks from identity providers), but not only for CAS.

The  `CasRealm` is replaced by the `Pac4jRealm` and the `CasSubjectFactory` by the `Pac4jsubjectFactory`.

Finally, you must use the `SecurityFilter` to secure an url, in addition of the default Shiro filters (like `roles`).


### 2.0 -> 2.2

The `config`, `clients`, `pac4jRealm`, `pac4jSubjectFactory` and `callbackFilter` components are already available using the `Pac4jIniEnvironment`.


### 1.4 - > 2.0

The `buji-pac4j` library strongly changes in version 2:

- the `core` and `servlet` modules are merged back into one main module
- the `ClientRealm` is replaced by the `Pac4jRealm` and the `ClientToken` by the `Pac4jToken`
- the `ClientUserFilter`, `ClientPermissionsAuthorizationFilter` and `ClientRolesAuthorizationFilter.java` are removed, more generally replaced by the `SecurityFilter` which ensures the url security (as usually in the pac4j world)
- the `CallbackFilter` replaces the `ClientFilter` to finish the login process for indirect clients (as usually in the pac4j world).


## Demo

The demo webapp: [buji-pac4j-demo](https://github.com/pac4j/buji-pac4j-demo) is available for tests and implements many authentication mechanisms: Facebook, Twitter, form, basic auth, CAS, SAML, OpenID Connect, JWT...


## Release notes

See the [release notes](https://github.com/bujiio/buji-pac4j/wiki/Release-Notes). Learn more by browsing the [buji-pac4j Javadoc](http://www.javadoc.io/doc/io.buji/buji-pac4j/2.2.0) and the [pac4j Javadoc](http://www.pac4j.org/apidocs/pac4j/1.9.6/index.html).


## Need help?

If you have any question, please use the following mailing lists:

- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)


## Development

The version 2.2.0-SNAPSHOT is under development.

Maven artifacts are built via Travis: [![Build Status](https://travis-ci.org/bujiio/buji-pac4j.png?branch=master)](https://travis-ci.org/bujiio/buji-pac4j) and available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j). This repository must be added in the Maven *pom.xml* file for example:

```xml
<repositories>
  <repository>
    <id>sonatype-nexus-snapshots</id>
    <name>Sonatype Nexus Snapshots</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```
