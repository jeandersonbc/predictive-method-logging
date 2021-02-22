#!/usr/bin/env bash

if [[ -z "$1" ]]; then
  echo Missing input settings '(<project>.sh)'
  echo Aborting.
  exit 1
fi

# shellcheck disable=SC1090
. "$1"

export PROJECT_PATH
export REV

BASEDIR="$(pwd)"
TOOLS_DIR="$BASEDIR/tools"

time "$TOOLS_DIR/pipeline-analysis.sh"
time "$TOOLS_DIR/pipeline-log-removal.sh"
time "$TOOLS_DIR/pipeline-codemetrics.sh"
time "$TOOLS_DIR/build-datasets.sh"
time "$TOOLS_DIR/pipeline-textual-features.sh"
