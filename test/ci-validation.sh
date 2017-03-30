curl -s "https://api.travis-ci.org/jobs/${TRAVIS_JOB_ID}/log.txt?deansi=true" > nonaui.log

expectation=`sed -n 's:.*<EXPECTED_RESULTS>\(.*\)</EXPECTED_RESULTS>.*:\1:p' nonaui.log | head -n 1`
nb_expectation=`sed -n ":;s/$expectation//p;t" nonaui.log | sed -n '$='`
echo "******** Check if all counter is OK ********"
if [ "$nb_expectation" == "2" ]; then
    echo SUCCESS
    let ret=0
else
    echo FAIL
    let ret=255
    exit $ret
fi

exit
