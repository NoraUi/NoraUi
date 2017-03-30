cd $(dirname $0)
cd ..
#mvn clean test javadoc:javadoc -Dcucumber.options="--tags @hello,@bonjour,@blog,@playToLogoGame,@jouerAuJeuDesLogos" -PscenarioInitiator,javadoc,unit-tests -Dmaven.test.failure.ignore=true
mvn clean compile

echo "NORA-UI is ready"

echo "******** start log *****"
echo "yes" | travis logs
echo "******** stop log *****"

rm -rf target

exit
