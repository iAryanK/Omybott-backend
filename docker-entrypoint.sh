#!/bin/sh
set -e

normalize_jdbc_url() {
  case "$1" in
    jdbc:*) printf '%s' "$1" ;;
    postgresql://* | postgres://*) printf 'jdbc:%s' "$1" ;;
    *) printf '%s' "$1" ;;
  esac
}

if [ -n "$DATABASE_URL" ] && [ -z "$SPRING_DATASOURCE_URL" ]; then
  export SPRING_DATASOURCE_URL="$(normalize_jdbc_url "$DATABASE_URL")"
elif [ -n "$SPRING_DATASOURCE_URL" ]; then
  export SPRING_DATASOURCE_URL="$(normalize_jdbc_url "$SPRING_DATASOURCE_URL")"
fi

exec java ${JAVA_OPTS} -jar /app/app.jar --server.port="${PORT:-8080}"
