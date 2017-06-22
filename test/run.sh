#!/usr/bin/env bash

#
# take noraui-datas-webservices from Maven Central and Start Web Services (REST)
wget -U "Any User Agent" https://oss.sonatype.org/service/local/repositories/snapshots/content/com/github/noraui/noraui-datas-webservices/1.0.0-SNAPSHOT/noraui-datas-webservices-1.0.0-20170622.195832-1.jar
ls -l
java -jar noraui-datas-webservices-1.0.0-SNAPSHOT.jar &
PID=$!
sleep 30
curl -s --header "Accept: application/json" http://localhost:8084/noraui/api/hello/columns > actual_hello_columns.json
curl -s --header "Accept: application/xml" http://localhost:8084/noraui/api/hello/columns > actual_hello_columns.xml
kill -9 $PID

cd $(dirname $0)
cd ..
mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package javadoc:javadoc sonar:sonar -Dsonar.host.url=https://sonarqube.com -Dsonar.organization=noraui -Dsonar.login=$SONAR_TOKEN -Dcucumber.options="--tags @hello,@bonjour,@blog,@playToLogoGame,@jouerAuJeuDesLogos" -PscenarioInitiator,javadoc,unit-tests -Dmaven.test.failure.ignore=true

#
# read Maven console
curl -s "https://api.travis-ci.org/jobs/${TRAVIS_JOB_ID}/log.txt?deansi=true" > nonaui.log

# check if BUILD FAILURE finded in logs
nb_failure=$(sed -n ":;s/BUILD FAILURE//p;t" nonaui.log | sed -n '$=')
if [ "$nb_failure" != "" ]; then
    echo "******** BUILD FAILURE find $nb_failure time in build"
    
    # patch for run any PR. (in PR case, the commiter do not have any sonar licence).
    sonar_governance=$(sed -n ":;s/Failed to execute goal org.sonarsource.scanner.maven:sonar-maven-plugin:3.0.1:sonar \(default-cli\) on project noraui: No license for governance//p;t" nonaui.log | sed -n '$=')
    if [ "$sonar_governance" == "" ]; then
        echo "******** BUILD FAILURE find $nb_failure time in build"
        exit 255
    fi
fi

counters=$(sed -n 's:.*<EXPECTED_RESULTS>\(.*\)</EXPECTED_RESULTS>.*:\1:p' nonaui.log | head -n 1)
nb_counters=$(sed -n ":;s/$counters//p;t" nonaui.log | sed -n '$=')
# 3 = 1 (real) + 2 counters (Excel and CSV)
if [ "$nb_counters" == "3" ]; then
    echo "******** All counter is SUCCESS"
else
    echo "******** All counter is FAIL"
    echo "$counters found $nb_counters times"
    exit 255
fi

echo ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,**/*/(##(*,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,/##//*,,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,***////###/,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*/*/////(###*,,,,,,,,,,,,,,,,,,,,,,,,,,,,,*###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///#/////(###,,,,,,,,,,,,,,,,,,,,,,,,,,,,**/(***,,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///##(/////###(,,,,,,,,,,,,,,,,,,,,,,,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(**////###/,,,,,,,,,,,,,,,,,,,,,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,*////(##(*,,,,,,,,,,,,,,,,,,,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,*////(##(*,,,,,,,,,,,,,,,,,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,**////###/,,,,,,,,,,,,,,,,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,,,*////(###*,,,,,,,,,,,,,,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,,,,,*////(##(,,,,,,,,,,,,,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,,,,,,**////###(,,,,,,,,,,,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,,,,,,,,**////###/,,,,,,,,,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,,,,,,,,,,*////(##(*,,,,,,,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,,,,,,,,,,,,*////(##(,,,,,,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,,,,,,,,,,,,,**///(###/,,,,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,,,,,,,,,,,,,,,*////(###*,,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,,,,,,,,,,,,,,,,,*////###(,,,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,,,,,,,,,,,,,,,,,,**//*/###(,,(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,,,,,,,,,,,,,,,,,,,,**///(###/(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,,,,,,,,,,,,,,,,,,,,,,*////(######///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,,,,,,,,,,,,,,,,,,,,,,,,*////#####///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(,,,,,,,,,,,,,,,,,,,,,,,,,,,,,*///*(###///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///###(********************************////(#///*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*///####################################(////////*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*//////////////////////////////////////////*///*/*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,*//////////////////////////////////////////**/**/*,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,,,,,,,,**,,,,**,,,,,,,,,,,,,,,,,,,/,,,,,*,/*,,,,,,,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,,,,,,,,/(#,,,*/,,,**,,,,,,,,,,*,,,#,,,,*/,,,,,,,,,,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,,,,,,,,/*,%*,*/,#/,,*#,/#,,,*,,/*,#,,,,*/,#*,,,,,,,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,,,,,,,,/*,,//*/,#,,,,(*/*,,,#(*/*,#*,,,*/,#*,,,,,,,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,,,,,,,,/*,,,*%/,(#,,((,/*,,*/,*#*,/%*,/#,,#*,,,,,,,,,,,,,,,,,,,,"
echo ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"

exit 0
