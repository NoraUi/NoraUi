echo "******** start log *****"
curl -s "https://api.travis-ci.org/jobs/${TRAVIS_JOB_ID}/log.txt?deansi=true" > toto.txt

cat toto.txt



echo "******** stop log *****"
