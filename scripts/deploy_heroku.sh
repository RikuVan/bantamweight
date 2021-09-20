#!/bin/bash
set -e

BANTAMWEIGHT_IMAGE="rikuvan/bantamweight:latest"

HEROKU_PROCESS_TYPE="web"
HEROKU_IMAGE_PATH="registry.heroku.com/${APP_NAME}/${HEROKU_PROCESS_TYPE}"

docker pull "${BANTAMWEIGHT_IMAGE}" && \
  docker tag "${BANTAMWEIGHT_IMAGE}" "${HEROKU_IMAGE_PATH}" && \
  docker push "${HEROKU_IMAGE_PATH}" && \
  heroku container:release "${HEROKU_PROCESS_TYPE}" --app "${APP_NAME}" && \
  echo "Your app is running at https://${APP_NAME}.herokuapp.com"