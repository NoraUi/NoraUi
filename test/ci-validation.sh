echo "******** start log *****"
curl -s "https://api.travis-ci.org/jobs/${TRAVIS_JOB_ID}/log.txt?deansi=true" > nonaui.log
cat nonaui.log

echo "******** count log *****"

cat nonaui.log | sed "s[INFO]\n/g" | wc -l




echo "******** stop log *****"
