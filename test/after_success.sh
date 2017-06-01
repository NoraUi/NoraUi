#!/bin/bash

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    echo "******** Starting deploy"
    mvn clean deploy -Pdeploy --settings test/mvnsettings.xml -DskipTests=true
    exit $?
fi
echo "******** END ********"
