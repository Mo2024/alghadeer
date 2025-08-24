#!/bin/bash

# Ask to build Spring Java
read -p "Do you want to build the Spring Java (alghadeer-backend) project? (y/N): " build_java

if [[ "$build_java" =~ ^[Yy]$ ]]; then
    echo "Building Spring Java project..."
    mvn clean package -DskipTests
else
    echo "Skipping Spring Java build."
fi

# Ask to build Docker image
read -p "Do you want to build and push the alghadeer-backend Docker image? (y/N): " build_docker

if [[ "$build_docker" =~ ^[Yy]$ ]]; then
    # Ask for version number
    read -p "Enter the version number (e.g., 1.0.5): " version

    echo "Building and pushing alghadeer-backend Docker image with tags:"
    echo " - preprod-$version"
    echo " - latest"

    docker buildx build -f Dockerfile \
      --push \
      --platform linux/amd64 \
      -t mohdosama2002/alghadeer-backend:preprod-$version \
      -t mohdosama2002/alghadeer-backend:latest \
      .
else
    echo "Skipping Docker build."
fi
