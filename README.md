## What is the buji-pac4j library ? [![Build Status](https://travis-ci.org/bujiio/buji-pac4j.png?branch=master)](https://travis-ci.org/bujiio/buji-pac4j)

The **buji-pac4j** library is a web multi-protocols client for [Apache Shiro](http://shiro.apache.org/).

It supports these 5 protocols on client side: 

1. OAuth (1.0 & 2.0)
2. CAS (1.0, 2.0, SAML, logout & proxy)
3. HTTP (form & basic auth authentications)
4. OpenID
5. SAML (2.0) (*still experimental*).

It's available under the Apache 2 license and based on my [pac4j](https://github.com/leleuj/pac4j) library.


## Providers supported

<table>
<tr><th>Provider</th><th>Protocol</th><th>Maven dependency</th><th>Client class</th><th>Profile class</th></tr>
<tr><td>CAS server</td><td>CAS</td><td>pac4j-cas</td><td>CasClient & CasProxyReceptor</td><td>CasProfile</td></tr>
<tr><td>CAS server using OAuth Wrapper</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>CasOAuthWrapperClient</td><td>CasOAuthWrapperProfile</td></tr>
<tr><td>DropBox</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>DropBoxClient</td><td>DropBoxProfile</td></tr>
<tr><td>Facebook</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>FacebookClient</td><td>FacebookProfile</td></tr>
<tr><td>GitHub</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>GitHubClient</td><td>GitHubProfile</td></tr>
<tr><td>Google</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>Google2Client</td><td>Google2Profile</td></tr>
<tr><td>LinkedIn</td><td>OAuth 1.0 & 2.0</td><td>pac4j-oauth</td><td>LinkedInClient & LinkedIn2Client</td><td>LinkedInProfile & LinkedIn2Profile</td></tr>
<tr><td>Twitter</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>TwitterClient</td><td>TwitterProfile</td></tr>
<tr><td>Windows Live</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>WindowsLiveClient</td><td>WindowsLiveProfile</td></tr>
<tr><td>WordPress</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>WordPressClient</td><td>WordPressProfile</td></tr>
<tr><td>Yahoo</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>YahooClient</td><td>YahooProfile</td></tr>
<tr><td>PayPal</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>PayPalClient</td><td>PayPalProfile</td></tr>
<tr><td>Vk</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>VkClient</td><td>VkProfile</td></tr>
<tr><td>Foursquare</td><td>OAuth 2.0</td><td>pac4j-oauth</td><td>FoursquareClient</td><td>FoursquareProfile</td></tr>
<tr><td>Bitbucket</td><td>OAuth 1.0</td><td>pac4j-oauth</td><td>BitbucketClient</td><td>BitbucketProfile</td></tr>
<tr><td>Web sites with basic auth authentication</td><td>HTTP</td><td>pac4j-http</td><td>BasicAuthClient</td><td>HttpProfile</td></tr>
<tr><td>Web sites with form authentication</td><td>HTTP</td><td>pac4j-http</td><td>FormClient</td><td>HttpProfile</td></tr>
<tr><td>Google</td><td>OpenID</td><td>pac4j-openid</td><td>GoogleOpenIdClient</td><td>GoogleOpenIdProfile</td></tr>
<tr><td>SAML Identity Provider</td><td>SAML 2.0</td><td>pac4j-saml</td><td>Saml2Client</td><td>Saml2Profile</td></tr>
</table>


## Technical description

This library has **just 9 classes**:

1. the **ClientFilter** class is called after the authentication at the provider: it creates the ClientToken to be used by the ClientRealm
2. the **ClientToken** class is the token representing the credentials and the profile of the user
3. the **ClientRealm** class is the realm responsible for authenticating a ClientToken: it finishes the authentication process by retrieving the profile of the authenticated user and computing its default roles and permissions
4. the **ClientSubjectFactory** class is the factory responsible for creating a Subject based on the authentication information (isRemembered...).
5. the **ShiroWebContext** class is a Shiro wrapper for the user request, response and session
6. the **ClientPermissionsAuthorizationFilter** class is a filter to protect the application if the user has not the right permissions
7. the **ClientRolesAuthorizationFilter** class is a filter to protect the application if the user has not the right roles
8. the **ClientUserFilter** class is a filter to protect the application if the user is not authenticated
9. the **NoAuthenticationException** class is an exception when no user profile is returned.

and is based on the <i>pac4j-*</i> libraries.

Learn more by browsing the [buji-pac4j Javadoc](http://www.pac4j.org/apidocs/buji-pac4j/index.html) and the [pac4j Javadoc](http://www.pac4j.org/apidocs/pac4j/index.html).


## How to use it ?

### Add the required dependencies

If you want to use a specific client support, you need to add the appropriate Maven dependency in the *pom.xml* file:

1. for OAuth support, the *pac4j-oauth* dependency is required
2. for CAS support, the *pac4j-cas* dependency is required
3. for HTTP support, the *pac4j-http* dependency is required
4. for OpenID support, the *pac4j-openid* dependency is required.

For example, to add OAuth support, add the following XML snippet:

    <dependency>
      <groupId>org.pac4</groupId>
      <artifactId>pac4j-oauth</artifactId>
      <version>1.5.0</version>
    </dependency>

As these snapshot dependencies are only available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/pac4j/), the appropriate repository may need be added in the *pom.xml* file also:

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

### Define the clients

If you want to authenticate at an OAuth or OpenID provider, at a CAS server or through HTTP, you have to define all the clients in the *shiro.ini* file:

    [main]
    facebookClient = org.pac4j.oauth.client.FacebookClient
    facebookClient.key = fbkey
    facebookClient.secret = fbsecret
    
    twitterClient = org.pac4j.oauth.client.TwitterClient
    twitterClient.key = twkey
    twitterClient.secret = twsecret
    
    simpleAuthenticator = org.pac4j.http.credentials.SimpleTestUsernamePasswordAuthenticator
    formClient = org.pac4j.http.client.FormClient
    formClient.loginUrl = http://localhost:8080/theForm.jsp
    formClient.usernamePasswordAuthenticator = $simpleAuthenticator
    
    basicAuthClient = org.pac4j.http.client.BasicAuthClient
    basicAuthClient.usernamePasswordAuthenticator = $simpleAuthenticator
    
    # the CAS server is started on localhost:8888/cas
    casClient = org.pac4j.cas.client.CasClient
    casClient.casLoginUrl = http://localhost:8888/cas/login
    
    # application is started on localhost:8080
    clients = org.pac4j.core.client.Clients
    clients.callbackUrl = http://localhost:8080/callback
    clients.clientsList = $facebookClient,$twitterClient,$formClient,$basicAuthClient,$casClient

### Customize the SecurityManager

To have a correct subject populated, you need to define the subject factory:

    [main]
    subjectFactory = io.buji.pac4j.ClientSubjectFactory
    securityManager.subjectFactory = $subjectFactory

### Define the filter and realm

To handle callback from providers, you need to define the appropriate filter:

    [main]
    clientsFilter = io.buji.pac4j.ClientFilter
    clientsFilter.clients = $clients
    clientsFilter.failureUrl = /error500.jsp
    
    [url]
    /callback = clientsFilter

To finish the authentication process after being callbacked by the provider, a specific realm must be declared:

    [main]
    clientsRealm = io.buji.pac4j.ClientRealm
    clientsRealm.defaultRoles = ROLE_USER
    clientsRealm.clients = $clients</code></pre>

### Protect the urls

You can protect your urls and force the user to be authenticated by a client by using one of the following filter: *ClientPermissionsAuthorizationFilter*, *ClientRolesAuthorizationFilter* or *ClientUserFilter*.
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

You can also explicitely compute a redirection url to a provider for authentication by using the *getRedirectionUrl* method. For example:

    <%
    ShiroWebContext context = new ShiroWebContext(request, response);
    %>
    <a href="<%=facebookClient.getRedirectionUrl(context,false,false)%>">Authenticate with Facebook</a>

### Use the appropriate profile

After successfull authentication, the first principal is the *typedId* (a unique id accross providers and users) and the second principal is the user profile:

    String typedId = (String) SecurityUtils.getSubject().getPrincipal();
    // common profile to all providers
    CommonProfile commonProfile = (CommonProfile) SecurityUtils.getSubject().getPrincipals().asList().get(1);

From the *CommonProfile*, you can retrieve the most common properties that all profiles share.
But you can also cast the user profile to the appropriate profile according to the provider used for authentication.
For example, after a Facebook authentication:
 
    // facebook profile
    FacebookProfile facebookProfile = (FacebookProfile) commonProfile;

Or for all the OAuth profiles, to get the access token:

    OAuthProfile oauthProfile = (OAuthProfile) commonProfile
    String accessToken = oauthProfile.getAccessToken();
    // or
    String accessToken = facebookProfile.getAccessToken();

### Demo

A demo with Facebook, Twitter, CAS, form authentication and basic auth authentication providers is available with [buji-pac4j-demo](https://github.com/leleuj/buji-pac4j-demo).


## Versions

The current version **1.2.3-SNAPSHOT** is under development. It's available on the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/io/buji) as a Maven dependency:

The last released version is the **1.2.2**:

    <dependency>
        <groupId>io.buji</groupId>
        <artifactId>buji-pac4j</artifactId>
        <version>1.2.2</version>
    </dependency>

See the [release notes](https://github.com/bujiio/buji-pac4j/wiki/Release-notes).

## Contact

For any question or problem, don't hesitate to post it on the [Shiro user mailing list](http://shiro-user.582556.n2.nabble.com/) or on the [Shiro developer mailing list](http://shiro-developer.582600.n2.nabble.com/).

Or please use the dedicated mailing lists:
- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)
