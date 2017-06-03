#!/bin/bash

mvn_version=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec)
echo "Maven version is $mvn_version"

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    echo "******** Starting deploy"
    mvn clean deploy -Psnapshot --settings test/mvnsettings.xml -DskipTests=true
    exit $?
fi
