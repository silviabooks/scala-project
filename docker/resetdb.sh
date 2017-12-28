#!/bin/sh
docker exec -it $(docker-compose ps -q advproject_scala) sbt "runMain devTests.MongoSeed"

