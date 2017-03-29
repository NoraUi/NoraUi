cd $(dirname $0)
cd ..
mvn clean test javadoc:javadoc -Dcucumber.options="--tags @hello,@bonjour,@blog,@playToLogoGame,@jouerAuJeuDesLogos" -PscenarioInitiator,javadoc,unit-tests -Dmaven.test.failure.ignore=true

ruby -v

echo "y" | gem install travis -v 1.8.8 --no-document

travis version

travis logs

echo "NORA-UI is ready"

rm -rf target

exit
