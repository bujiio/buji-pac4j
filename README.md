<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-shiro.png" width="300" />
</p>

If you already use the [Shiro](http://shiro.apache.org) security library, you need to use the `buji-pac4j` official security extension to support multi-protocols authentication: CAS, OAuth, SAML...

If you are looking for a full security library, you can directly use a pac4j implementation for your environment: [`j2e-pac4j`](https://github.com/pac4j/j2e-pac4j) for J2E, [`play-pac4j`](https://github.com/pac4j/play-pac4j) for Play framework v2, [`spring-webmvc-pac4j`](https://github.com/pac4j/spring-webmvc-pac4j) for Spring MVC / Boot... See all the frameworks and tools supported by [pac4j](http://www.pac4j.org).

`buji-pac4` supports many authentication mechanisms, called [**clients**](https://github.com/pac4j/pac4j/wiki/Clients):

- **indirect / stateful clients** are for UI when the user authenticates once at an external provider (like Facebook, a CAS server...) or via a local form (or basic auth popup).

See the [authentication flows](https://github.com/pac4j/pac4j/wiki/Authentication-flows).

| The authentication mechanism you want | The `pac4j-*` submodule(s) you must use
|---------------------------------------|----------------------------------------
| OAuth (1.0 & 2.0): Facebook, Twitter, Google, Yahoo, LinkedIn, Github... | `pac4j-oauth`
| CAS (1.0, 2.0, 3.0, SAML, logout, proxy) | `pac4j-cas`
| SAML (2.0) | `pac4j-saml`
| OpenID Connect (1.0) | `pac4j-oidc`
| HTTP (form, basic auth)<br />+ LDAP<br />or Relational DB<br />or MongoDB<br />or Stormpath<br />or CAS REST API| `pac4j-http`<br />+ `pac4j-ldap`<br />or `pac4j-sql`<br />or `pac4j-mongo`<br />or `pac4j-stormpath`<br />or `pac4j-cas`
| Google App Engine UserService | `pac4j-gae`
| OpenID | `pac4j-openid`


## How to use it?

First, you need to add a dependency on this library as well as on the appropriate `pac4j` submodules. Then, you must define the [**clients**](https://github.com/pac4j/pac4j/wiki/Clients) for authentication.

Define the `ClientFilter` and `ClientRealm` to finish authentication processes.

Use the `ClientPermissionsAuthorizationFilter`, `ClientRolesAuthorizationFilter` and `ClientUserFilter` filters to secure the urls of your web application (using the `clientName` property for authentication).

Just follow these easy steps:


### Add the required dependencies (`buji-pac4j-*` + `pac4j-*` libraries)

You need to add a dependency on either `buji-pac4j-core` or `buji-pac4j-servlet` (<em>groupId</em>: **org.pac4j**, *version*: **1.4.0**). Both are intended to be used in web-based environments.
`buji-pac4j-servlet` is to be used in servlet environments and depends on `buji-pac4j-core`. It is the library to be used for most Java
web projects (e.g. anything running on Tomcat, Glassfish, Jetty, etc.) `buji-pac4j-core` is a library without any servlet dependencies
and thus is meant to be used with some newer Java web frameworks which do not support servlets such as Play Framework or Vert.x.

You also need to add dependencies on the appropriate `pac4j` submodules (<em>groupId</em>: **org.pac4j**, *version*: **1.8.1**): the `pac4j-oauth` dependency for OAuth support, the `pac4j-cas` dependency for CAS support, the `pac4j-ldap` module for LDAP authentication, ...

All released artifacts are available in the [Maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cpac4j).


### Define the configuration (`Clients` + `XXXClient`)

Each authentication mechanism (Facebook, Twitter, a CAS server...) is defined by a client (implementing the `org.pac4j.core.client.Client` interface). All clients must be gathered in a `org.pac4j.core.client.Clients` class.
You have to define all the clients in the *shiro.ini* file:

    [main]
    facebookClient = org.pac4j.oauth.client.FacebookClient
    facebookClient.key = fbkey
    facebookClient.secret = fbsecret
    
    twitterClient = org.pac4j.oauth.client.TwitterClient
    twitterClient.key = twkey
    twitterClient.secret = twsecret
    
    simpleAuthenticator = org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator
    formClient = org.pac4j.http.client.indirect.FormClient
    formClient.loginUrl = http://localhost:8080/theForm.jsp
    formClient.authenticator = $simpleAuthenticator
    
    casClient = org.pac4j.cas.client.CasClient
    casClient.casLoginUrl = http://localhost:8888/cas/login
    
    clients = org.pac4j.core.client.Clients
    clients.callbackUrl = http://localhost:8080/callback
    clients.clientsList = $facebookClient,$twitterClient,$formClient,$casClient

"http://localhost:8080/callback" is the url of the callback endpoint (see below).


### Define the callback endpoint

Indirect clients rely on external identity providers (like Facebook) and thus require to define a callback endpoint in the application where the user will be redirected after login at the identity provider.  
Thus, you need to define the appropriate `ClientFilter` in your *shiro.ini* file:

    [main]
    clientsFilter = io.buji.pac4j.ClientFilter
    clientsFilter.clients = $clients
    clientsFilter.failureUrl = /error500.jsp
    
    clientsRealm = io.buji.pac4j.ClientRealm
    clientsRealm.defaultRoles = ROLE_USER
    clientsRealm.clients = $clients
    
    subjectFactory = io.buji.pac4j.ClientSubjectFactory
    securityManager.subjectFactory = $subjectFactory
        
    [urls]
    /callback = clientsFilter

Notice you have two additional elements for Shiro:
- a realm: `ClientRealm` with which you can define the default granted roles and permissions in addition to the ones provided by any `AuthorizationGenerator` attached to the client used for authentication
- a subject factory: `ClientSubjectFactory`.


### Protect an url

You can protect an url and require the user to be authenticated  by a client or have the appropriate roles or permissions by using one of the following filter: `ClientUserFilter`, `ClientPermissionsAuthorizationFilter` or `ClientRolesAuthorizationFilter`.  
For example:

    [main]
    facebookRoles = io.buji.pac4j.filter.ClientRolesAuthorizationFilter
    facebookRoles.client = $facebookClient
    formRoles = io.buji.pac4j.filter.ClientRolesAuthorizationFilter
    formRoles.client = $formClient
    casRoles = io.buji.pac4j.filter.ClientRolesAuthorizationFilter
    casRoles.client = $casClient

    [urls] 
    /facebook/** = facebookRoles[ROLE_USER]
    /form/** = formRoles[ROLE_USER]
    /cas/** = casRoles[ROLE_USER]
    /callback = clientsFilter
    /logout = logout
    /** = anon


### Get redirection urls

You can also explicitly compute a redirection url to a provider by using the `getRedirectAction` method of the client, in order to create an explicit link for login. For example with Facebook:

    <%
      ShiroWebContext context = new ShiroWebContext(request, response);
    %>
    <a href="<%=facebookClient.getRedirectAction(context, false).getLocation()%>">Authenticate with Facebook</a>


### Get the user profile

After a successful authentication, the first Shiro principal is the *typedId* (a unique id across providers and users) and the second principal is the user profile:

    String typedId = (String) SecurityUtils.getSubject().getPrincipal();
    // common profile to all providers
    CommonProfile commonProfile = (CommonProfile) SecurityUtils.getSubject().getPrincipals().asList().get(1);

The retrieved profile is at least a `CommonProfile`, from which you can retrieve the most common properties that all profiles share. But you can also cast the user profile to the appropriate profile according to the provider used for authentication. For example, after a Facebook authentication:
 
    FacebookProfile facebookProfile = (FacebookProfile) commonProfile;


### Logout

For logout, like for any other Shiro webapp, use the default logout filter (in the *shiro.ini* file):

    /logout = logout


## Demo

The demo webapp: [buji-pac4j-demo](https://github.com/pac4j/buji-pac4j-demo) is available for tests and implement many authentication mechanisms: Facebook, Twitter, form, basic auth, CAS, SAML...


## Release notes

See the [release notes](https://github.com/bujiio/buji-pac4j/wiki/Release-Notes). Learn more by browsing the [buji-pac4j Javadoc](http://www.pac4j.org/apidocs/buji-pac4j/1.4.0/index.html) and the [pac4j Javadoc](http://www.pac4j.org/apidocs/pac4j/1.8.1/index.html).


## Need help?

If you have any question, please use the following mailing lists:

- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)

Or the [Shiro user mailing list](http://shiro-user.582556.n2.nabble.com/) and [Shiro developer mailing list](http://shiro-developer.582600.n2.nabble.com/).


## Development

The next version 1.4.1-SNAPSHOT is under development.

Maven artifacts are built via Travis: [![Build Status](https://travis-ci.org/bujiio/buji-pac4j.png?branch=master)](https://travis-ci.org/bujiio/buji-pac4j) and available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j). This repository must be added in the Maven *pom.xml* file for example:

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
