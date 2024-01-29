#!/bin/sh
set -e

mvn clean package -DskipTests

cp target/rabbit-0.0.1.jar dock/app1/target
cp target/rabbit-0.0.1.jar dock/app2/target

cd dock

docker-compose stop

cd app1
docker build -t app1 .

cd ../app2
docker build -t app2 .
cd ../

docker-compose up -d
