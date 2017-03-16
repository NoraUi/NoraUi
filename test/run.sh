cd $(dirname $0)
cd ..
clean clean deploy javadoc:javadoc -Dcucumber.options="--tags @hello,@bonjour,@blog,@playToLogoGame,@jouerAuJeuDesLogos" -PscenarioInitiator,javadoc,unit-tests -Dmaven.test.failure.ignore=true
# java -jar target/flags-rest-service-0.1.1.jar &
# PID=$!
# sleep 15

# kill -9 $PID

echo "NORA-UI is ready"

rm -rf target

exit
