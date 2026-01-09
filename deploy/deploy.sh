#!/usr/bin/env bash
set -euo pipefail

cd /srv/slavery-home-challenge-api

echo "Current directory:"
pwd

echo "Docker version:"
docker --version

echo "Docker Compose version:"
docker compose version

echo "Pulling latest images..."
docker compose -f docker-compose.prod.yml pull

echo "Restarting containers..."
docker compose -f docker-compose.prod.yml up -d --remove-orphans

echo "Done ✅"
