#!/bin/sh

# Compile and package into jar
echo "[1] Compiling and packaging in jar"
./mvnw clean package >/dev/null

# Create the docker image and name the target
echo "[2] Creating docker image"
docker build -t gstep/coinbase-client . >/dev/null

# Create container based on the image
# Expose 8080 port on host
echo "[3] Creating container: exposes 8080 port"
docker run -d --name=ticker-client -p 8080:8080 gstep/coinbase-client >/dev/null
