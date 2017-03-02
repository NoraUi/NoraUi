cd $(dirname $0)
cd ..
mvn clean compile
# java -jar target/flags-rest-service-0.1.1.jar &
# PID=$!
# sleep 15

# kill -9 $PID

echo "NORA-UI is ready"

rm -rf target

exit
