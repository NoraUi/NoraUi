cd $(dirname $0)
cd ..
mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package javadoc:javadoc sonar:sonar -Dsonar.host.url=https://sonarqube.com -Dsonar.organization=noraui -Dsonar.login=$SONAR_TOKEN -Dcucumber.options="--tags @hello,@bonjour,@blog,@playToLogoGame,@jouerAuJeuDesLogos" -PscenarioInitiator,javadoc,unit-tests -Dmaven.test.failure.ignore=true

curl -s "https://api.travis-ci.org/jobs/${TRAVIS_JOB_ID}/log.txt?deansi=true" > nonaui.log

counters=`sed -n 's:.*<EXPECTED_RESULTS>\(.*\)</EXPECTED_RESULTS>.*:\1:p' nonaui.log | head -n 1`
nb_counters=`sed -n ":;s/$counters//p;t" nonaui.log | sed -n '$='`

# check if BUILD FAILURE finded in logs
nb_failure=`sed -n ":;s/BUILD FAILURE//p;t" nonaui.log | sed -n '$='`
if [ "$nb_failure" != "0" ]; then
    echo "******** BUILD FAILURE find $nb_failure time in build"
    exit 255
fi

# 3 = 1 (real) + 2 counters (Excel and CSV)
if [ "$nb_counters" == "3" ]; then
    echo "******** All counter is SUCCESS"
else
    echo "******** All counter is FAIL"
    exit 255
fi

exit 0
