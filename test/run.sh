cd $(dirname $0)
cd ..
mvn clean test javadoc:javadoc sonar:sonar org.jacoco:jacoco-maven-plugin:prepare-agent -Dsonar.host.url=https:/sonarqube.com -Dsonar.login=a1db2f2fa275c0472a6111d6bd773d878e375aeb -Dcucumber.options="--tags @hello,@bonjour,@blog,@playToLogoGame,@jouerAuJeuDesLogos" -PscenarioInitiator,javadoc,unit-tests -Dmaven.test.failure.ignore=true

curl -s "https://api.travis-ci.org/jobs/${TRAVIS_JOB_ID}/log.txt?deansi=true" > nonaui.log

expectation=`sed -n 's:.*<EXPECTED_RESULTS>\(.*\)</EXPECTED_RESULTS>.*:\1:p' nonaui.log | head -n 1`
nb_expectation=`sed -n ":;s/$expectation//p;t" nonaui.log | sed -n '$='`

# check if [INFO] BUILD FAILURE
#

# 3 = 1 (real) + 2 counters (Excel and CSV)
if [ "$nb_expectation" == "3" ]; then
    echo "******** All counter is SUCCESS"
else
    echo "******** All counter is FAIL"
    exit 255
fi

exit 0
