<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-shiro.png" width="300" />
</p>


The `buji-pac4j` project is an **easy and powerful security library for Shiro web applications and web services** which supports authentication and authorization, but also advanced features like CSRF protection.
It's based on Java 8, Shiro 1.4 and on the **[pac4j security engine](https://github.com/pac4j/pac4j) v3**. It's available under the Apache 2 license.

[**Main concepts and components:**](http://www.pac4j.org/docs/main-concepts-and-components.html)

1) A [**client**](http://www.pac4j.org/docs/clients.html) represents an authentication mechanism. It performs the login process and returns a user profile. An indirect client is for web applications authentication while a direct client is for web services authentication:

&#9656; OAuth - SAML - CAS - OpenID Connect - HTTP - OpenID - Google App Engine - Kerberos - LDAP - SQL - JWT - MongoDB - CouchDB - IP address - REST API

2) An [**authorizer**](http://www.pac4j.org/docs/authorizers.html) is meant to check authorizations on the authenticated user profile(s) or on the current web context:

&#9656; Roles / permissions - Anonymous / remember-me / (fully) authenticated - Profile type, attribute -  CORS - CSRF - Security headers - IP address, HTTP method

3) The `SecurityFilter` protects an url by checking that the user is authenticated and that the authorizations are valid, according to the clients and authorizers configuration. If the user is not authenticated, it performs authentication for direct clients or starts the login process for indirect clients

4) The `CallbackFilter` finishes the login process for an indirect client

5) The `LogoutFilter` logs out the user from the application and triggers the logout at the identity provider level


## Usage

### 1) [Add the required dependencies](https://github.com/bujiio/buji-pac4j/wiki/Dependencies)

### 2) Define:

### - the [security configuration](https://github.com/bujiio/buji-pac4j/wiki/Security-configuration)
### - the [callback configuration](https://github.com/bujiio/buji-pac4j/wiki/Callback-configuration), only for web applications
### - the [logout configuration](https://github.com/bujiio/buji-pac4j/wiki/Logout-configuration)

### 3) [Apply security](https://github.com/bujiio/buji-pac4j/wiki/Apply-security)

### 4) [Get the authenticated user profiles](https://github.com/bujiio/buji-pac4j/wiki/Get-the-authenticated-user-profiles)


## Demo

The demo webapp: [buji-pac4j-demo](https://github.com/pac4j/buji-pac4j-demo) is available for tests and implements many authentication mechanisms: Facebook, Twitter, form, basic auth, CAS, SAML, OpenID Connect, JWT...


## Versions

The latest released version is the [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.buji/buji-pac4j/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.buji/buji-pac4j), available in the [Maven central repository](https://repo.maven.apache.org/maven2).
The [next version](https://github.com/pac4j/spring-security-pac4j/wiki/Next-version) is under development.

See the [release notes](https://github.com/bujiio/buji-pac4j/wiki/Release-Notes). Learn more by browsing the [pac4j documentation](http://www.pac4j.org/3.3.x/docs/index.html) and the [buji-pac4j Javadoc](http://www.javadoc.io/doc/io.buji/buji-pac4j/4.0.0).

See the [migration guide](https://github.com/bujiio/buji-pac4j/wiki/Migration-guide) as well.


## Need help?

If you need commercial support (premium support or new/specific features), contact us at [info@pac4j.org](mailto:info@pac4j.org).

If you have any questions, want to contribute or be notified about the new releases and security fixes, please subscribe to the following [mailing lists](http://www.pac4j.org/mailing-lists.html):

- [pac4j-users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j-developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)
- [pac4j-announce](https://groups.google.com/forum/?hl=en#!forum/pac4j-announce)
- [pac4j-security](https://groups.google.com/forum/#!forum/pac4j-security)
