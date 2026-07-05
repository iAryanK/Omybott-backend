#!/bin/sh
set -e

exec java ${JAVA_OPTS} -jar /app/app.jar --server.port="${PORT:-8080}"
