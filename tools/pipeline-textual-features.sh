#!/usr/bin/env bash

# Loads subject settings from an external bash script if informed
# shellcheck source=/dev/null
[[ -n "$1" ]] && . "$1"
PROJECT_NAME="$(basename "$PROJECT_PATH")"

BASEDIR="$(pwd)"
TOOLSDIR="$BASEDIR/tools"
INPUT_DIR="$BASEDIR/out/log-removal/$PROJECT_NAME/nolog"
OUTPUT_DIR="$BASEDIR/out/textual-features/$PROJECT_NAME"
CODEMETRICS_DATASET="$BASEDIR/out/dataset/$PROJECT_NAME"

run_token_extractor() {
  local input_path=$1
  local output_dir=$2

  echo "input=$input_path"
  echo "output=$output_dir"

  echo "Running token extractor"
  rm -rf "$output_dir" && mkdir -p "$output_dir"

  "$TOOLSDIR"/bin/java-token-extractor "$input_path" &>"$output_dir"/output.log

  mv tokens.json "$output_dir"
  echo Done
}

run_validation() {
  local txt_features_path="$1/tokens.json"
  local codemetrics_path="$2/dataset_full.csv"

  "$TOOLSDIR"/validate_features.py "$txt_features_path" "$codemetrics_path"

  if [[ ! "$?" ]]; then
    echo "Validation failed!"
    diff dump-df.csv dump-json-df.csv >"$OUTPUT_DIR"/validation.diff
  fi
}

time run_token_extractor "${INPUT_DIR}" "${OUTPUT_DIR}"
time run_validation "${OUTPUT_DIR}" "${CODEMETRICS_DATASET}"
