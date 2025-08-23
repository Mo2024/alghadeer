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
    echo "Building and pushing alghadeer-frontend Docker image..."
    sudo docker build -f Dockerfile.test --push --platform linux/amd64 -t mohdosama2002/alghadeer-frontend:preprod-1.0.0 .
else
    echo "Skipping Docker build."
fi