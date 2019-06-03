#!/usr/bin/env bash

set -e

image=${1:-frontend}
port=${2:-8080}

printf "Starting container... "
container_id=$(docker run -d -p $port:80 $image)
if [[ $? -ne 0 ]]
then
	echo "FAIL"
	echo "Could not run image $image" >/dev/stderr
	exit 1
fi
echo "OK"

cleanup() {
	printf "Stopping $container_id... "
	docker stop $container_id >/dev/null
	echo "OK"
}

trap cleanup ERR


printf "Checking container is running... "
ps=$(docker ps --filter "id=$container_id" --format '{{ .ID }}')
if [ -z "$ps" ]
then
	echo "FAIL"
	echo "Container $container_id is not running" >/dev/stderr
	exit 1
fi
echo "OK"

printf "Checking container responds to GET request... "
url="http://localhost:$port"
curl --silent --fail $url >/dev/null
if [[ $? -ne 0 ]]
then
	echo "FAIL"
	echo "Container $container_id is not responding to $url" >/dev/stderr
	exit 1
fi
echo "OK"

cleanup
