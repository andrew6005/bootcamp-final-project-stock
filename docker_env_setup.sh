#!/usr/bin/env bash
set -e

docker compose stop heatmap-ui-app || true
docker rm heatmap-ui-app || true

mvn clean package
docker build -t stockmap:0.0.1 -f Dockerfile .
docker compose up -d postgres redis heatmap-ui-app

echo "Docker environment is running at http://localhost:8080/heatmap"
