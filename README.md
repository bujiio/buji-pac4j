<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-shiro.png" width="300" />
</p>

The `buji-pac4j` project is a **bridge from pac4j to Shiro** to push the pac4j security context into the Shiro security context.  
It's based on the **[pac4j security engine](https://github.com/pac4j/pac4j)**. It's available under the Apache 2 license.

| buji-pac4j   | JDK | pac4j | Shiro | Operating philosophy        | Usage of Lombok | Status           |
|--------------|-----|-------|-------|-----------------------------|-----------------|------------------|
| version >= 9 | 17  | v6    | v1.11 | Bridge only                 | Yes             | Production ready |
| version >= 8 | 11  | v5    | v1.9  | Bridge only                 | No              | Production ready |
| version >= 6 | 11  | v5    | v1.8  | Standalone security library | No              | Production ready |
| version >= 5 | 8   | v4    | v1.5  | Standalone security library | No              | Production ready |

**It must be used with a [pac4j security library](https://www.pac4j.org/implementations.html)**:
- certainly, the [javaee-pac4j](https://github.com/pac4j/jee-pac4j) implementation (which has the same filters as `buji-pac4j` version <= 7.x)
- or maybe, if you use Spring MVC, the [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j) implementation.

While **it is always better to directly use a pac4j security library alone**, this bridge can be used to keep legacy software and avoid full migration.


## Usage

### 1) [Add the required dependencies](https://github.com/bujiio/buji-pac4j/wiki/Dependencies)

### 2) The bridge is automatically installed

See the [configuration](https://github.com/bujiio/buji-pac4j/blob/master/src/main/resources/buji-pac4j-default.ini) that is loaded by default.

### 3) Install, configure and use the pac4j security library

You must refer to the documentation of the pac4j security library you use: [javaee-pac4j](https://github.com/pac4j/jee-pac4j) (or maybe [spring-webmvc-pac4j](https://github.com/pac4j/spring-webmvc-pac4j)).


## Demo

Shiro demo: [buji-pac4j-demo](https://github.com/pac4j/buji-pac4j-demo).


## Versions

The latest released version is the [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.buji/buji-pac4j/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.buji/buji-pac4j), available in the [Maven central repository](https://repo.maven.apache.org/maven2).
The [next version](https://github.com/bujiio/buji-pac4j/wiki/Next-version) is under development.

See the [release notes](https://github.com/bujiio/buji-pac4j/wiki/Release-Notes). Learn more by browsing the [pac4j documentation](https://www.javadoc.io/doc/org.pac4j/pac4j-core/6.0.1/index.html) and the [buji-pac4j Javadoc](http://www.javadoc.io/doc/io.buji/buji-pac4j/9.0.0).

See the [migration guide](https://github.com/bujiio/buji-pac4j/wiki/Migration-guide) as well.


## Need help?

You can use the [mailing lists](https://www.pac4j.org/mailing-lists.html) or the [commercial support](https://www.pac4j.org/commercial-support.html).
