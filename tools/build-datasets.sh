#!/usr/bin/env bash

# Loads subject settings from an external bash script if informed
# shellcheck source=/dev/null
[[ -n "$1" ]] && . "$1"

BASEDIR="$(pwd)"
TOOLSDIR="$BASEDIR/tools"

PROJECT_NAME="$(basename "$PROJECT_PATH")"
CODEMETRICS_DIR="$BASEDIR/out/codemetrics/$PROJECT_NAME"
OUTPUT_DIR="$BASEDIR/out/dataset/$PROJECT_NAME"

NOLOG_OUT="$CODEMETRICS_DIR/nolog"
COPIED_OUT="$CODEMETRICS_DIR/copied"

rm -rf "$OUTPUT_DIR" && mkdir -p "$OUTPUT_DIR"

pushd "$OUTPUT_DIR" || exit 1
{
time python3 "$TOOLSDIR/build-datasets.py" \
             "$CODEMETRICS_DIR/nolog" \
             "$CODEMETRICS_DIR/copied"

} | tee dataset.log
popd || exit 1
