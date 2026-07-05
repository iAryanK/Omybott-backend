#!/bin/sh
set -e

if [ -n "$DATABASE_URL" ] && [ -z "$SPRING_DATASOURCE_URL" ]; then
  export SPRING_DATASOURCE_URL="jdbc:${DATABASE_URL}"
fi

exec java ${JAVA_OPTS} -jar /app/app.jar --server.port="${PORT:-8080}"
