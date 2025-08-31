#!/bin/bash

# Ask to build Angular project
read -p "Do you want to build the Angular (alghadeer-frontend) project? (y/N): " build_angular

if [[ "$build_angular" =~ ^[Yy]$ ]]; then
    echo "Building Angular project..."
    ng build --configuration=production
else
    echo "Skipping Angular build."
fi

# Ask to build Docker image
read -p "Do you want to build and push the alghadeer-frontend Docker image? (y/N): " build_docker

if [[ "$build_docker" =~ ^[Yy]$ ]]; then
    # Ask for version number
    read -p "Enter the version number (e.g., 1.1.3): " version

    echo "Building and pushing alghadeer-frontend Docker image with tags:"
    echo " - prod-$version"
    echo " - latest"

    docker buildx build -f Dockerfile.prod \
      --push \
      --platform linux/amd64 \
      -t mohdosama2002/alghadeer-frontend:prod-$version \
      -t mohdosama2002/alghadeer-frontend:latest \
      .
else
    echo "Skipping Docker build."
fi
