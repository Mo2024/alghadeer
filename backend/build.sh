#!/bin/bash

# Ask to build Spring Java
read -p "Do you want to build the Spring Java project? (y/N): " build_java

if [[ "$build_java" =~ ^[Yy]$ ]]; then
    echo "Building Spring Java project..."
    mvn clean package -DskipTests
else
    echo "Skipping Spring Java build."
fi

# Ask to build Docker image
read -p "Do you want to build and push the Docker image? (y/N): " build_docker

if [[ "$build_docker" =~ ^[Yy]$ ]]; then
    echo "Building and pushing Docker image..."
    sudo docker build --push --platform linux/amd64 -t mohdosama2002/alghadeer-backend:preprod-1.0.0 .
else
    echo "Skipping Docker build."
fi