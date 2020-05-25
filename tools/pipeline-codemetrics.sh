#!/usr/bin/env bash

# Loads subject settings from an external bash script if informed
# shellcheck source=/dev/null
[[ -n "$1" ]] && . "$1"

PROJECT_NAME="$(basename "$PROJECT_PATH")"

BASEDIR="$(pwd)"
TOOLS_DIR="$BASEDIR/tools"
OUTPUT_BASEDIR="$BASEDIR/out/codemetrics/$PROJECT_NAME"

run_ck() {
  local variant=$1

  echo "RUNNING CK with variant $variant"

  local target="$BASEDIR/out/log-removal/$PROJECT_NAME/$variant"
  local output="$OUTPUT_BASEDIR/$variant"
  local output_log="$output/ck.log"

  if [[ ! -d $target ]]; then
    echo "Oops, unable to find \"$target\" dir"
    exit 1
  fi

  # Clean state
  rm -rf "$output" && mkdir -p "$output"

  ck_jar="$TOOLS_DIR/ck-0.6.3-SNAPSHOT-jar-with-dependencies.jar"
  java_args="-Xmx14g -Dlog4j.configuration=file:$TOOLS_DIR/log4j.xml"

  # shellcheck disable=SC2086
  java $java_args -jar "$ck_jar" "$target" false 100 false &>"$output_log" &&
    mv class.csv method.csv "$output"

  echo "CKClassResult $(grep -c 'INFO CKClassResult' "$output_log")"
  echo "Errors $(grep -c 'ERROR error' "$output_log")"
}

# Compares CK output with our analysis numbers
# Results must be consistent
sanity_check() {
  local ck_method_csv="$OUTPUT_BASEDIR/copied/method.csv"
  local selection_log="$BASEDIR/out/analysis/$PROJECT_NAME/log_placement_analyzer.log"
  local analysis_values=$(tail -n 1 "$selection_log" | awk '{print $2,$4}')
  python3 "$TOOLS_DIR/sanity_check.py" $ck_method_csv $analysis_values
}

run_ck copied
run_ck nolog
sanity_check
