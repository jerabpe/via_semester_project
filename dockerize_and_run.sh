#!/bin/bash
docker image rm jerabpe/via-w2w
docker build -t jerabpe/via-w2w .

docker-compose down
docker rmi jerabpe/via-w2w
docker-compose up