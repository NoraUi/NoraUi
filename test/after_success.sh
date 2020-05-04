#!/bin/bash

echo "TRAVIS_REPO_SLUG is $TRAVIS_REPO_SLUG"
echo "TRAVIS_BRANCH is $TRAVIS_BRANCH"
echo "TRAVIS_PULL_REQUEST is $TRAVIS_PULL_REQUEST"
echo "AUTHOR_NAME is $AUTHOR_NAME"
  		  
if [ "$TRAVIS_REPO_SLUG" == 'NoraUi/NoraUi' ] && [ "$TRAVIS_BRANCH" == 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    
    if [ "$AUTHOR_NAME" != 'Travis-CI' ]; then
        echo "******** publish javadoc"
        cd $HOME
        git config --global user.email "sgrillon@hotmail.fr"
        git config --global user.name "Travis-CI"
        git clone --quiet --branch=master https://${GITHUB_TOKEN}@github.com/NoraUi/NoraUi NoraUi > /dev/null
        # Commit and Push the Changes
        cd NoraUi
        git rm -rf ./docs
        cp -Rf /home/travis/build/NoraUi/NoraUi/target/apidocs ./docs
        git add -f .
        git commit -m "Lastest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to NoraUi"
        git push -fq origin master
    fi
    
    mvn_version=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.5.0:exec)
    echo "Maven version is $mvn_version"
    if [[ $mvn_version == *"-SNAPSHOT" ]]; then
        echo "******** Starting deploy snapshot"
        mvn clean deploy -Psnapshot --settings test/mvnsettings.xml -DskipTests=true
        exit $?
    fi
fi
