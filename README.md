# NoraUi
Non-Regression Automation for User Interfaces

# Technical documentation

[Public website for NoraUi (Non-Regression Automation for User Interfaces)](https://noraui.github.io/).

[![Javadocs](https://javadoc.io/badge/com.github.noraui/noraui.svg)](https://noraui.github.io/NoraUi)

# News 
See [change log](https://noraui.github.io/changelog.txt) for details
* 2017-09-??: NoraUi [3.0.0] is in progress.
* 2017-09-21: NoraUi [2.6.3] is released.

# how to migrate from [2.x.x] to [3.x.x]

* -Dcucumber.options="--tags '@tag1 or @tag2 or @tag3 or @tag4'"
* NoraUi use Oracle Jdbc Driver
1. if you use Oracle Db provider, configure your env with OTN: Sample(https://github.com/sgrillon14/MavenSampleOracleJdbc)
2. if you do not use Oracle Db provider, remove com.oracle.jdbc:ojdbc8 dependency

# Supported languages

These are the currently supported languages

* English
* French

Does NoraUi not support your language? Please help us with a PR!

# Continuous Integration status
[![Build Status](https://travis-ci.org/NoraUi/NoraUi.svg?branch=master)](https://travis-ci.org/NoraUi/NoraUi)
[![Quality Gate](https://sonarcloud.io/api/badges/gate?key=com.github.noraui:noraui)](https://sonarcloud.io/dashboard/index/com.github.noraui:noraui)
[![SonarCloud Coverage](https://sonarcloud.io/api/badges/measure?key=com.github.noraui%3Anoraui&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=com.github.noraui:noraui)
[![SonarCloud Bugs](https://sonarcloud.io/api/badges/measure?key=com.github.noraui%3Anoraui&metric=bugs)](https://sonarcloud.io/component_measures/metric/reliability_rating/list?id=com.github.noraui%3Anoraui)
[![SonarCloud Vulnerabilities](https://sonarcloud.io/api/badges/measure?key=com.github.noraui%3Anoraui&metric=vulnerabilities)](https://sonarcloud.io/component_measures/metric/security_rating/list?id=com.github.noraui%3Anoraui)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ee5c5b13365d4de5ba6b1ec4f8b984d2)](https://www.codacy.com/app/noraui/NoraUi?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=NoraUi/NoraUi&amp;utm_campaign=Badge_Grade)

# Contributors

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
    <version>3.0.0-SNAPSHOT</version>
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
* Java 8
* Maven
* JUnit

# Run Anywhere
![RunAnywhere](/screenshots/plateforme.png)

# License

[![license](https://img.shields.io/github/license/NoraUi/NoraUi.svg)](https://github.com/NoraUi/NoraUi/blob/master/LICENSE) See LICENSE for details

# Contributing

The [issue tracker](https://github.com/NoraUi/NoraUi/issues) is the preferred channel for bug reports, features requests and submitting pull requests.

For pull requests, editor preferences are available in the [editor config](.editorconfig) for easy use in common text editors. Read more and download plugins at <http://editorconfig.org>.

![footer](https://noraui.github.io/img/end.png)
