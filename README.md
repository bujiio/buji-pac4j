<h2>What is buji-oauth ?</h2>

<b>buji-oauth</b> is a web OAuth client for <a href="http://shiro.apache.org/">Shiro</a> to :
<ol>
<li>delegate authentication and permissions to an OAuth provider (i.e. the user is redirected to the OAuth provider to log in)</li>
<li>(in the application) retrieve the profile of the authorized user after successfull authentication and permissions acceptation (at the OAuth provider).</li>
</ol>

It's available under the Apache 2 license and based on my <a href="https://github.com/leleuj/scribe-up">scribe-up</a> library (which deals with OAuth authentication and user profile retrieval).


<h2>OAuth providers supported</h2>

<table>
<tr><td>Web site</td><td>Protocol</td><td>Provider</td><td>Profile</td></tr>
<tr><td>DropBox</td><td>OAuth 1.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/DropBoxProvider.html">DropBoxProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/dropbox/DropBoxProfile.html">DropBoxProfile</a></td></tr>
<tr><td>Facebook</td><td>OAuth 2.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/FacebookProvider.html">FacebookProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/facebook/FacebookProfile.html">FacebookProfile</a></td></tr>
<tr><td>Github</td><td>OAuth 2.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/GitHubProvider.html">GitHubProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/github/GitHubProfile.html">GitHubProfile</a></td></tr>
<tr><td>Google</td><td>OAuth 1.0 & 2.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/GoogleProvider.html">GoogleProvider</a> & <a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/Google2Provider.html">Google2Provider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/google/GoogleProfile.html">GoogleProfile</a> & <a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/google2/Google2Profile.html">Google2Profile</a></td></tr>
<tr><td>LinkedIn</td><td>OAuth 1.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/LinkedInProvider.html">LinkedInProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/linkedin/LinkedInProfile.html">LinkedInProfile</a></td></tr>
<tr><td>Twitter</td><td>OAuth 1.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/TwitterProvider.html">TwitterProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/twitter/TwitterProfile.html">TwitterProfile</a></td></tr>
<tr><td>Windows Live</td><td>OAuth 2.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/WindowsLiveProvider.html">WindowsLiveProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/windowslive/WindowsLiveProfile.html">WindowsLiveProfile</a></td></tr>
<tr><td>WordPress</td><td>OAuth 2.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/WordPressProvider.html">WordPressProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/wordpress/WordPressProfile.html">WordPressProfile</a></td></tr>
<tr><td>Yahoo</td><td>OAuth 1.0</td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/provider/impl/YahooProvider.html">YahooProvider</a></td><td><a href="http://javadoc.leleuj.cloudbees.net/scribe-up/1.3.0-SNAPSHOT/org/scribe/up/profile/yahoo/YahooProfile.html">YahooProfile</a></td></tr>
</table>

Follow the guide to <a href="https://github.com/leleuj/scribe-up/wiki/Extend-or-add-a-new-provider">extend or add a new provider</a>.

<h2>Technical description</h2>

This library has <b>just 9 classes</b> :
<ol>
<li>the <b>OAuthFilter</b> class is called after OAuth authentication : it creates the OAuthToken to be used by the OAuthRealm</li>
<li>the <b>OAuthToken</b> class is the token for an OAuth authentication (= OAuth credentials + the user profile)</li>
<li>the <b>OAuthRealm</b> class is the realm responsible for authenticating OAuthToken</ tokens : it calls the OAuth provider to get the access token and the user profile and computes the authorities.</li>
<li>the <b>OAuthAuthenticationException</b> class is the exception for OAuth authentication issue</li>
<li>the <b>ShiroUserSession</b> class is a wrapper in Shiro for the user session</li>
<li>the <b>OAuthFormAuthenticationFilter</b> class is a filter to protect the application if the user is not authenticated</li>
<li>the <b>OAuthPermissionsAuthorizationFilter</b> class is a filter to protect the application if the user has not the right permissions</li>
<li>the <b>OAuthRolesAuthorizationFilter</b> class is a filter to protect the application if the user has not the right roles</li>
<li>the <b>OAuthUserFilter</b> class is a filter to protect the application if the user is not authenticated</li>
</ol>

and the <a href="https://github.com/leleuj/scribe-up">scribe-up</a> library.

<h2>Code sample</h2>

If you want to authenticate at Facebook, you have to define the Facebook provider in the <i>shiro.ini</i> file :
<pre><code>[main]
oauthProvider = org.scribe.up.provider.impl.FacebookProvider
oauthProvider.key = mykey
oauthProvider.secret = mysecret
oauthProvider.callbackUrl = http://localhost:8080/shiro-oauth</code></pre>
You first have to define a filter to handle callback from Facebook :
<pre><code>[main]
oauthFilter = org.buji.oauth.OAuthFilter
oauthFilter.provider = $oauthProvider
oauthFilter.failureUrl = /error.jsp</code></pre>
Then the dedicated Facebook realm for authentication, user profile retrieval and default granted roles / permissions :
<pre><code>[main]
oauthRealm = org.buji.oauth.OAuthRealm
oauthRealm.defaultRoles = ROLE_USER
#oauthRealm.defaultPermissions = defaultPermission
oauthRealm.provider = $oauthProvider</code></pre>
Finally, only if needed, you may want to redirect for certain urls the user in some case (not authenticated, not the right permissions...) :
<pre><code>rolesFilter = org.buji.oauth.filter.OAuthRolesAuthorizationFilter
rolesFilter.provider = $oauthProvider</code></pre>
With the following "security configuration" :
<pre><code>[urls]
/protected/** = rolesFilter[ROLE_USER]
/shiro-oauth = oauthFilter
/** = anon</code></pre>

If you have multiple providers, you can multiply filters and realms by setting specific endpoint url, but a better solution (<b>new feature of version 1.1.0</b>) is to define a providers definition to group providers :
<pre><code>facebookProvider = org.scribe.up.provider.impl.FacebookProvider
facebookProvider.key = mykfbey
facebookProvider.secret = myfbsecret
twitterProvider = org.scribe.up.provider.impl.TwitterProvider
twitterProvider.key = mytwkey
twitterProvider.secret = mytwsecret
providersDefinition = org.scribe.up.provider.ProvidersDefinition
providersDefinition.baseUrl = http://localhost:8080/shiro-oauth
providersDefinition.providers = $facebookProvider, $twitterProvider</code></pre>
And use this providers definition in filter and realm :
<pre><code>providersRealm = io.buji.oauth.OAuthRealm
providersRealm.defaultRoles = ROLE_USER
providersRealm.providersDefinition = $providersDefinition
providersFilter = io.buji.oauth.OAuthFilter
providersFilter.providersDefinition = $providersDefinition
providersFilter.failureUrl = /error.jsp</code></pre>

After successfull authentication, the first principal is the <i>typedId</i> (a unique id accross providers and users) and the second principal is the user profile :
<pre><code>String typedId = (String) SecurityUtils.getSubject().getPrincipal();
// user profile
UserProfile userProfile = (UserProfile) SecurityUtils.getSubject().getPrincipals().asList().get(1);
// facebook profile
FacebookProfile facebookProfile = (FacebookProfile) userProfile;
// common profile to all providers
CommonProfile commonProfile = (CommonProfile) userProfile;</code></pre>
If you want to interact more with the OAuth provider, you can retrieve the access token from the (OAuth) profile :
<pre><code>OAuthProfile oauthProfile = (OAuthProfile) userProfile;
String accessToken = oauthProfile.getAccessToken();
// or
String accesstoken = facebookProfile.getAccessToken();</code></pre>

A demo with Facebook and Twitter providers is available with <a href="https://github.com/leleuj/buji-oauth-demo.git">buji-oauth-demo</a>.
The old demo for buji-oauth version 1.0.0 is available with <a href="https://github.com/leleuj/buji-oauth-demo-1.0.0.git">buji-oauth-demo-1.0.0</a>.

<h2>Versions</h2>

The last released version is the <b>1.0.0</b>.

The current version : <i>1.1.0-SNAPSHOT</i> is under development, it's available on <a href="https://oss.sonatype.org/content/repositories/snapshots/">Sonatype snapshots repository</a> as Maven dependency :
<pre><code>&lt;dependency&gt;
    &lt;groupId&gt;io.buji&lt;/groupId&gt;
    &lt;artifactId&gt;buji-oauth&lt;/artifactId&gt;
    &lt;version&gt;1.1.0-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;</code></pre>

See the <a href="https://github.com/bujiio/buji-oauth/wiki/Release-Notes">release notes</a>.

<h2>Contact</h2>

For any question or problem, don't hesitate to post it on the <a href="http://shiro-user.582556.n2.nabble.com/">Shiro user mailing list</a> or on the <a href="http://shiro-developer.582600.n2.nabble.com/">Shiro developer mailing list</a>.

