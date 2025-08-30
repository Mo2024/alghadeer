#!/bin/bash

# Get the container ID of the frontend container
container_id=$(docker ps --filter "name=frontend" --format "{{.ID}}" | head -n 1)

# Get the first 2 characters of the container ID
short_id=${container_id:0:2}

echo "Frontend container ID: $container_id (short: $short_id)"

# Exec into the container and cd into /app
docker exec -it "$container_id" bash -c "cd /app && exec bash"

