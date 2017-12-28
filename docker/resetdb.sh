#!/bin/sh
docker exec -t $(docker-compose ps -q advproject_scala) sbt "runMain devTests.MongoSeed"

