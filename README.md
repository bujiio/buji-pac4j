<h2>What is the buji-pac4j library ?</h2>

The <b>buji-pac4j</b> library is a web multi-protocols client for <a href="http://shiro.apache.org/">Apache Shiro</a>.

It supports these 4 protocols on client side : 
<ol>
<li>OAuth (1.0 & 2.0)</li>
<li>CAS (1.0, 2.0, SAML, logout & proxy)</li>
<li>HTTP (form & basic auth authentications)</li>
<li>OpenID.</li>
</ol>

It's available under the Apache 2 license and based on my <a href="https://github.com/leleuj/pac4j">pac4j</a> library.


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
<tr><td>Web sites with basic auth authentication</td><td>HTTP</td><td>pac4j-http</td><td>BasicAuthClient</td><td>HttpProfile</td></tr>
<tr><td>Web sites with form authentication</td><td>HTTP</td><td>pac4j-http</td><td>FormClient</td><td>HttpProfile</td></tr>
<tr><td>MyOpenId</td><td>OpenID</td><td>pac4j-openid</td><td>MyOpenIdClient</td><td>MyOpenIdProfile</td></tr>
<tr><td>Google</td><td>OpenID</td><td>pac4j-openid</td><td>GoogleOpenIdClient</td><td>GoogleOpenIdProfile</td></tr>
</table>


<h2>Technical description</h2>

This library has <b>just 9 classes</b> :
<ol>
<li>the <b>ClientFilter</b> class is called after the authentication at the provider : it creates the ClientToken to be used by the ClientRealm</li>
<li>the <b>ClientToken</b> class is the token representing the credentials and the profile of the user</li>
<li>the <b>ClientRealm</b> class is the realm responsible for authenticating a ClientToken : it finishes the authentication process by retrieving the profile of the authenticated user and computing its default roles and permissions</li>
<li>the <b>ShiroWebContext</b> class is a Shiro wrapper for the user request, response and session</li>
<li>the <b>ClientPermissionsAuthorizationFilter</b> class is a filter to protect the application if the user has not the right permissions</li>
<li>the <b>ClientRolesAuthorizationFilter</b> class is a filter to protect the application if the user has not the right roles</li>
<li>the <b>ClientUserFilter</b> class is a filter to protect the application if the user is not authenticated</li>
<li>the <b>FilterHelper</b> class has some common logic for filters</li>
<li>the <b>NoAuthenticationException</b> class is an exception when no user profile is returned</li>
</ol>

and is based on the <i>pac4j-*</i> libraries.

Learn more by browsing the <a href="http://www.pac4j.org/apidocs/buji-pac4j/index.html">buji-pac4j Javadoc</a> and the <a href="http://www.pac4j.org/apidocs/pac4j/index.html">pac4j Javadoc</a>.


<h2>How to use it ?</h2>

<h3>Add the required dependencies</h3>

If you want to use a specific client support, you need to add the appropriate Maven dependency in the <i>pom.xml</i> file :
<ul>
<li>for OAuth support, the <i>pac4j-oauth</i> dependency is required</li>
<li>for CAS support, the <i>pac4j-cas</i> dependency is required</li>
<li>for HTTP support, the <i>pac4j-http</i> dependency is required</li>
<li>for OpenID support, the <i>pac4j-openid</i> dependency is required.</li>
</ul>

For example, to add OAuth support, add the following XML snippet :
<pre><code>&lt;dependency&gt;
    &lt;groupId&gt;org.pac4j&lt;/groupId&gt;
    &lt;artifactId&gt;pac4j-oauth&lt;/artifactId&gt;
    &lt;version&gt;1.4.2-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;</code></pre>

As these snapshot dependencies are only available in the <a href="https://oss.sonatype.org/content/repositories/snapshots/org/pac4j/">Sonatype Snapshots repository</a>, the appropriate repository must be added in the <i>pom.xml</i> file also :
<pre><code>&lt;repositories&gt;
  &lt;repository&gt;
    &lt;id&gt;sonatype-nexus-snapshots&lt;/id&gt;
    &lt;name&gt;Sonatype Nexus Snapshots&lt;/name&gt;
    &lt;url&gt;https://oss.sonatype.org/content/repositories/snapshots&lt;/url&gt;
    &lt;releases&gt;
      &lt;enabled&gt;false&lt;/enabled&gt;
    &lt;/releases&gt;
    &lt;snapshots&gt;
      &lt;enabled&gt;true&lt;/enabled&gt;
    &lt;/snapshots&gt;
  &lt;/repository&gt;
&lt;/repositories&gt;</code></pre>

<h3>Define the clients</h3>

If you want to authenticate at an OAuth or OpenID provider, at a CAS server or through HTTP, you have to define all the clients in the <i>shiro.ini</i> file :
<pre><code>[main]
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

myopenidClient = org.pac4j.openid.client.MyOpenIdClient

# application is started on localhost:8080
clients = org.pac4j.core.client.Clients
clients.callbackUrl = http://localhost:8080/callback
clients.clientsList = $facebookClient,$twitterClient,$formClient,$basicAuthClient,$casClient,$myopenidClient</code></pre>

<h3>Define the filter and realm</h3>

To handle callback from providers, you need to define the appropriate filter :
<pre><code>[main]
clientsFilter = io.buji.pac4j.ClientFilter
clientsFilter.clients = $clients
clientsFilter.failureUrl = /error500.jsp

[url]
/callback = clientsFilter</code></pre>

To finish the authentication process after being callbacked by the provider, a specific realm must be declared :
<pre><code>[main]
clientsRealm = io.buji.pac4j.ClientRealm
clientsRealm.defaultRoles = ROLE_USER
clientsRealm.clients = $clients</code></pre>

<h3>Protect the urls</h3>

You can protect your urls and force the user to be authenticated by a client by using one of the following filter : <i>ClientPermissionsAuthorizationFilter</i>, <i>ClientRolesAuthorizationFilter</i> or <i>ClientUserFilter</i>.

For example :
<pre><code>[main]
facebookRoles = io.buji.pac4j.filter.ClientRolesAuthorizationFilter
facebookRoles.client = $facebookClient
formRoles = io.buji.pac4j.filter.ClientRolesAuthorizationFilter
formRoles.client = $formClient
casRoles = io.buji.pac4j.filter.ClientRolesAuthorizationFilter
casRoles.client = $casClient
myopenidRoles = io.buji.pac4j.filter.ClientRolesAuthorizationFilter
myopenidRoles.client = $myopenidClient

[urls] 
/facebook/** = facebookRoles[ROLE_USER]
/form/** = formRoles[ROLE_USER]
/cas/** = casRoles[ROLE_USER]
/myopenid/** = myopenidRoles[ROLE_USER]
/callback = clientsFilter
/logout = logout
/** = anon</code></pre>

<h3>Get redirection urls</h3>

You can also explicitely compute a redirection url to a provider for authentication by using the <i>getRedirectionUrl</i> method. For example :
<pre><code>&lt;%
 ShiroWebContext context = new ShiroWebContext(request, response);
%&gt;
&lt;a href="&lt;%=facebookClient.getRedirectionUrl(context)%&gt;"&gt;Authenticate with Facebook&lt;/a&gt;</code></pre>

<h3>Use the appropriate profile</h3>

After successfull authentication, the first principal is the <i>typedId</i> (a unique id accross providers and users) and the second principal is the user profile :
<pre><code>String typedId = (String) SecurityUtils.getSubject().getPrincipal();
// common profile to all providers
CommonProfile commonProfile = (CommonProfile) SecurityUtils.getSubject().getPrincipals().asList().get(1);</code></pre>
From the <i>CommonProfile</i>, you can retrieve the most common properties that all profiles share.
But you can also cast the user profile to the appropriate profile according to the provider used for authentication.
For example, after a Facebook authentication : 
<pre><code>// facebook profile
FacebookProfile facebookProfile = (FacebookProfile) commonProfile;</code></pre>
Or for all the OAuth profiles, to get the access token :
<pre><code>OAuthProfile oauthProfile = (OAuthProfile) commonProfile
String accessToken = oauthProfile.getAccessToken();
// or
String accessToken = facebookProfile.getAccessToken();</code></pre>

<h3>Demo</h3>

A demo with Facebook, Twitter, CAS, form authentication, basic auth authentication and myopenid.com providers is available with <a href="https://github.com/leleuj/buji-pac4j-demo">buji-pac4j-demo</a>.


## Versions

The current version **1.2.2-SNAPSHOT** is under development. It's available on the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/io/buji) as a Maven dependency :

The last released version is the **1.2.1** :

    <dependency>
        <groupId>io.buji</groupId>
        <artifactId>buji-pac4j</artifactId>
        <version>1.2.1</version>
    </dependency>

See the [release notes](https://github.com/bujiio/buji-pac4j/wiki/Release-notes).

## Contact

For any question or problem, don't hesitate to post it on the [Shiro user mailing list](http://shiro-user.582556.n2.nabble.com/) or on the [Shiro developer mailing list](http://shiro-developer.582600.n2.nabble.com/).

Or please use the dedicated mailing lists :
- [pac4j users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)

