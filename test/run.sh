#!/usr/bin/env bash

#
# take noraui-datas-webservices from Maven Central and Start Web Services (REST)
wget -O noraui-datas-webservices-1.0.0.jar 'https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=com.github.noraui&a=noraui-datas-webservices&v=1.0.0-SNAPSHOT&p=jar'

java -jar noraui-datas-webservices-1.0.0.jar &
PID=$!
sleep 30
curl -s --header "Accept: application/json" http://localhost:8084/noraui/api/hello/columns > actual_hello_columns.json
curl -s --header "Accept: application/xml" http://localhost:8084/noraui/api/hello/columns > actual_hello_columns.xml

ls -l

echo "Let's look at the actual results: `cat actual_hello_columns.json`"
echo "And compare it to: `cat test/expected_hello_columns.json`"
if diff -w test/expected_hello_columns.json actual_hello_columns.json
    then
        echo SUCCESS
        let ret=0
    else
        echo FAIL
        let ret=255
        exit $ret
fi

echo "Let's look at the actual results: `cat actual_hello_columns.xml`"
echo "And compare it to: `cat test/expected_hello_columns.xml`"
if diff -w test/expected_hello_columns.xml actual_hello_columns.xml
    then
        echo SUCCESS
        let ret=0
    else
        echo FAIL
        let ret=255
        exit $ret
fi
echo "******** noraui-datas-webservices STARTED"

#
#
# start NoraUi part =>
cd $(dirname $0)
cd ..
mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package javadoc:javadoc sonar:sonar -Dsonar.host.url=https://sonarqube.com -Dsonar.organization=noraui -Dsonar.login=$SONAR_TOKEN -Dcucumber.options="--tags '@hello or @bonjour or @blog or @playToLogoGame or @jouerAuJeuDesLogos'" -PscenarioInitiator,javadoc,unit-tests -Dmaven.test.failure.ignore=true

#
# kill Web Services (REST)
kill -9 $PID
echo "******** noraui-datas-webservices STOPED"

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

counters1=$(sed -n 's:.*\[Excel\] > <EXPECTED_RESULTS_1>\(.*\)</EXPECTED_RESULTS_1>.*:\1:p' nonaui.log | head -n 1)
nb_counters1=$(sed -n ":;s/$counters1//p;t" nonaui.log | sed -n '$=')

counters2=$(sed -n 's:.*\[Excel\] > <EXPECTED_RESULTS_2>\(.*\)</EXPECTED_RESULTS_2>.*:\1:p' nonaui.log | head -n 1)
nb_counters2=$(sed -n ":;s/$counters2//p;t" nonaui.log | sed -n '$=')

# 3 = 1 (real) + 2 counters (Excel and CSV)
if [ "$nb_counters1" == "3" ] && [ "$nb_counters2" == "3" ]; then
    echo "******** All counter is SUCCESS"
else
    echo "******** All counter is FAIL"
    echo "$counters1 found $nb_counters1 times"
    echo "$counters2 found $nb_counters2 times"
    pwd
    ls -l
    cat target/reports/html/index.html
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
