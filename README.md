# NoraUi
Non-Regression Automation for User Interfaces

# Technical documentation

[Public website for NoraUi (Non-Regression Automation for User Interfaces)](https://noraui.github.io/).

[![Javadocs](https://img.shields.io/badge/javadoc-3.1.1-brightgreen.svg)](https://noraui.github.io/NoraUi)

# News 
See [change log](https://noraui.github.io/changelog.txt) for details
* 2019-xx-xx: NoraUi [4.0.0] is in progress.
* 2019-xx-xx: NoraUi [3.3.0] is in progress.
* 2018-05-24: NoraUi [3.2.0] is released.
* 2018-03-19: NoraUi [3.1.3] is released.
* 2018-03-14: NoraUi [3.1.2] is released.
* 2018-03-06: NoraUi [3.1.1] is released.
* 2018-03-02: NoraUi [3.1.0] is released.
* 2018-02-05: NoraUi [3.0.2] is released.
* 2017-12-07: NoraUi [3.0.1] is released.
* 2017-11-12: NoraUi [3.0.0] is released.
* 2017-09-21: NoraUi [2.6.3] **is prohibited.**

# How to migrate from [3.x.x] to [4.x.x]

* You can use all new features from cucumber v4.x.x. (Use Cucumber Expressions)
* You need use Cucumber Expressions instead of regular expressions
* You need use java 8 instead of java 7.
* Rename all `jenkinsresourcespath` by `ciToolResourcesPath` in your CI/CD

# How to migrate from [2.x.x] to [3.x.x] (mandatory)

* -Dcucumber.options="--tags '@tag1 or @tag2 or @tag3 or @tag4'"
* NoraUi use Oracle Jdbc Driver
1. if you use Oracle Db provider, use com.oracle.jdbc:ojdbc8 dependency and configure your env with OTN: Sample(https://github.com/sgrillon14/MavenSampleOracleJdbc) (default)
2. if you do not use Oracle Db provider, just exclude com.oracle.jdbc:ojdbc8 dependency in the noraui dependency call within your pom.xml. You can follow these instructions at https://maven.apache.org/guides/introduction/introduction-to-optional-and-excludes-dependencies.html. It should look something like that :
```xml
<dependency>
    <groupId>com.github.noraui</groupId>
    <artifactId>noraui</artifactId>
    <version>${noraui.version}</version>
    <exclusions>
        <exclusion>
            <groupId>com.oracle.jdbc</groupId>
            <artifactId>ojdbc8</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```
* Change all "noraui" import to "com.github.noraui"
* Change all `private static Logger logger = Logger.getLogger` by `private static final Logger logger = LoggerFactory.getLogger`
* Change all `import org.apache.log4j.Logger;` by `import org.slf4j.Logger;` and `import org.slf4j.LoggerFactory;`
* Change com.github.noraui.browser.DriverFactory#setProperty(String key, Properties propertyFile) to com.github.noraui.browser.DriverFactory#getProperty(String key, Properties propertyFile)
* Change com.github.noraui.browser.DriverFactory#setIntProperty(String key, Properties propertyFile) to com.github.noraui.browser.DriverFactory#getIntProperty(String key, Properties propertyFile)
* replace all step constructors by `com.google.inject.Inject` (inject pages)
* replace loadAuthentication(String browser) by loadAuthentication(Cookie cookie)
* replace PhantomJs by Chrome headless
* replace springframework RestTemplate by OkHttp

# Supported languages

These are the currently supported languages

* English
* French

Does NoraUi not support your language? Please help us with a [PR](https://github.com/NoraUi/NoraUi/pulls)!

# Continuous Integration status
[![Build Status](https://travis-ci.org/NoraUi/NoraUi.svg?branch=master)](https://travis-ci.org/NoraUi/NoraUi)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FNoraUi%2FNoraUi.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2FNoraUi%2FNoraUi?ref=badge_shield)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.github.noraui%3Anoraui&metric=alert_status)](https://sonarcloud.io/dashboard/index/com.github.noraui:noraui)
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.github.noraui%3Anoraui&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=com.github.noraui:noraui)
[![SonarCloud Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.github.noraui%3Anoraui&metric=bugs)](https://sonarcloud.io/component_measures/metric/reliability_rating/list?id=com.github.noraui%3Anoraui)
[![SonarCloud Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.github.noraui%3Anoraui&metric=vulnerabilities)](https://sonarcloud.io/component_measures/metric/security_rating/list?id=com.github.noraui%3Anoraui)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ee5c5b13365d4de5ba6b1ec4f8b984d2)](https://www.codacy.com/app/noraui/NoraUi?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=NoraUi/NoraUi&amp;utm_campaign=Badge_Grade)

# :octocat: Contributors

[![GitHub contributors](https://img.shields.io/github/contributors/NoraUi/NoraUi.svg)](https://github.com/NoraUi/NoraUi/graphs/contributors)
[![GitHub closed pull requests](https://img.shields.io/github/issues-pr/NoraUi/NoraUi.svg)](https://github.com/NoraUi/NoraUi/pulls)
[![GitHub issues](https://img.shields.io/github/issues/NoraUi/NoraUi.svg)](https://github.com/NoraUi/NoraUi/issues)

# Gitter chat
[![Join the chat at https://gitter.im/NoraUiChat](https://badges.gitter.im/NoraUiChat/NoraUi.svg)](https://gitter.im/NoraUiChat/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# How to use

Stable:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.noraui/noraui/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.noraui/noraui)

SNAPSHOT:

```xml
<dependency>
    <groupId>com.github.noraui</groupId>
    <artifactId>noraui</artifactId>
    <version>3.3.0-SNAPSHOT</version>
</dependency>
```

```xml
<repository>
    <id>sonatype-snapshots</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
</repository>
```

# Technology

* Gherkin
* Cucumber
* Selenium
* Webdriver
* Java 8
* Maven
* Google guice
* JUnit / Mockito / Cucumber

# Run Anywhere
![RunAnywhere](/screenshots/plateforme.png)

# License

[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FNoraUi%2FNoraUi.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2FNoraUi%2FNoraUi?ref=badge_shield) [![license](https://img.shields.io/github/license/NoraUi/NoraUi.svg)](https://github.com/NoraUi/NoraUi/blob/master/LICENSE) See LICENSE for details

**/!\ CAUTION: NoraUi V2.x.x is prohibited.**

These versions use the following dependency whose copyrights are not respected.
```
     <GroupId>com.github.noraui</groupId>
     <ArtifactId>cucumber-metrics</artifactId>
```

This dependency comes from a Pull Request (Cucumber-JVM: Metrics) proposal by [sgrillon14](https://github.com/sgrillon14) to open source library "[Cucumber-JVM](https://github.com/cucumber/cucumber-jvm)". Nobody has permission to copy this code let alone claim to be creator of it. This was noted in the README.md of the GitHub repository. This part has been officially integrated into NoraUi in V3.x.x which has an AGPL license.

These versions use the following dependency whose copyrights are not respected.

```
    <GroupId>com.github.noraui</groupId>
    <ArtifactId>ojdbc7</artifactId>
```

The NoraUi project had the temporary authorization of sonatype to publish this version. This was temporary because Oracle finally did not agree. Oracle has declined NoraUi's proposal and the deposit is now removed. The use by the NoraUi team was tolerated until the end of 2017 (It was noted in the README.md of the GitHub repository.) But is now forbidden for all.


# Contributing

Before create issue check [![Ask at StackOverflow](https://img.shields.io/badge/StackOverflow-noraui-F5C10E.svg?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAnFBMVEUAAADs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PHs8PG3iEVjAAAAM3RSTlMAAQMGBwkLDA0QExweKS4wMzc4PkdJT1JdY2dvc3WDjpiam6avtcfMz9ng4ubp7fHz9/tqGqSaAAAAfUlEQVQYGV3BBw6CQABFwb/YUOy9IIpgb8i7/92ErBLCjKzGKzAqa185tVTmhCR9/ZmoJy1gY2QN4DIy3oNjXVY3gvu0HfN09dPZwXsdRsqNb8GkY5rblE9NuRWZNF7O9r4sx5sfEjKeJJDlDv2zAwJREAhEQSBQCYgKUfEFJ7oYF2usUEAAAAAASUVORK5CYII=)](https://stackoverflow.com/questions/ask?tags=noraui)

The [issue tracker](https://github.com/NoraUi/NoraUi/issues) is the preferred channel for bug reports, features requests and submitting pull requests.

For pull requests, read [CONTRIBUTING](/CONTRIBUTING) before.

* Step 1: Create a fork.
* Step 2: Create a branch on this new fork.
* Step 3: Sync your travis-ci account and guithub account.

![howtocontribute-travis-ci-sync](/screenshots/howtocontribute-travis-ci-sync.png)
* Step 4: Flick the repository switch on.

![howtocontribute-travis-ci-on](/screenshots/howtocontribute-travis-ci-on.png)
* Step 5: Add OTN login/password in Travis-ci environnement.

![howtocontribute-travis-ci-env-var](/screenshots/howtocontribute-travis-ci-env-var.png)
* Step 6: Valid if Travis status is OK.
* Step 7: Create pull request.

![footer](https://noraui.github.io/img/end.png)
