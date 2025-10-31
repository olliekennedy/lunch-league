#!/usr/bin/env bash
set -euo pipefail

if command -v fly >/dev/null 2>&1; then FLY=fly
elif command -v flyctl >/dev/null 2>&1; then FLY=flyctl
else echo "ERROR: fly CLI not found." >&2; exit 1; fi

[ -f fly.toml ] || { echo "ERROR: fly.toml missing."; exit 1; }

APP_NAME=$(awk -F'=' '/^app *=/ {gsub(/"/,"");gsub(/ /,"");print $2}' fly.toml | head -1)
PRIMARY_REGION=$(awk -F'=' '/^primary_region *=/ {gsub(/"/,"");gsub(/ /,"");print $2}' fly.toml | head -1)
[ -n "${APP_NAME}" ] || { echo "ERROR: app name not found."; exit 1; }
[ -n "${PRIMARY_REGION}" ] || { echo "ERROR: primary_region not set."; exit 1; }

# Check if app exists, create if not
if ! ${FLY} apps list | grep -q "^${APP_NAME}\b"; then
  echo "==> App ${APP_NAME} does not exist. Creating..."
  ${FLY} apps create "${APP_NAME}"
fi

echo "==> App: ${APP_NAME}"
echo "==> Primary region: ${PRIMARY_REGION}"

if ! ls build/libs/*.jar >/dev/null 2>&1; then
  echo "==> No jar found; performing local build (tests run)"
  [ -x ./gradlew ] || { echo "ERROR: gradlew missing."; exit 1; }
  ./gradlew --quiet clean build
fi

ARTIFACT=$(find build/libs -maxdepth 1 -type f -name "*.jar" | head -1 || true)
[ -n "${ARTIFACT}" ] && echo "==> Using artifact: ${ARTIFACT}"

VERSION_LABEL="${VERSION_LABEL:-$(date -u +%Y%m%d%H%M%S)-$(git rev-parse --short HEAD 2>/dev/null || echo nogit)}"
echo "==> Version label: ${VERSION_LABEL}"

echo "==> Deploying"
${FLY} deploy --image-label "${VERSION_LABEL}" --wait-timeout 300

echo "==> Final status"
${FLY} status --app "${APP_NAME}" || true
echo "==> Done"
