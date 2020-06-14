#!/usr/bin/env bash
#
# Runs experiment pipeline on subject.
#
# This pipeline helps us to characterize a subject by extracting
# information about Java source files and the pervasiveness of log statements
#
# Pipeline:
#   1. Pre-condition check: if it's a git repo, repo must be at specific revision
#   2. Find all .java files on repo and classify them
#   3. Run log statements analysis
#
# -Have fun

# Loads subject settings from an external bash script if informed
# shellcheck source=/dev/null
[[ -n "$1" ]] && . "$1"

# Subject settings
[[ -z "$PROJECT_PATH" ]] &&
  echo Missing PROJECT_PATH variable &&
  echo Aborted. &&
  exit 1

check_rev_precondition() {
  if [[ ! -d ".git" ]]; then
    echo Not a git repo. Skipping rev checking...
    return 0
  fi

  [[ -z "$REV" ]] &&
    echo Missing REV variable &&
    echo Aborted. &&
    exit 1

  local current="$(git rev-parse HEAD)"
  if [ ! "$REV" == "$current" ]; then
    echo check_rev_rule: Expected "$REV" but was "$current"
    echo Aborted
    exit 1
  fi
}

find_java_sources() {
  echo Looking for java sources
  local out="$OUTPUT_DIR/java_sources.txt"

  find . -path '*.java' -type f >"$out"
  echo Found $(cat "$out" | wc -l) java files

  local classify_out="$OUTPUT_DIR/java_sources_classified.txt"
  python3 "$PYSCRIPTS/classify.py" "$out" >"$classify_out"

  # classify only appends a new column to the original out
  rm $out

  cat $classify_out | awk '{print $2}' | sort | uniq -c | sort
}

analyze_log_statements() {
  echo Running analysis
  local classify_out="$OUTPUT_DIR/java_sources_classified.txt"

  "$TOOLSDIR"/log-placement-analyzer "$classify_out" 1>"$OUTPUT_DIR/log_placement_analyzer.log"

  echo "$(($(cat "$OUTPUT_DIR/log-placement.csv" | wc -l) - 1))" log statements "(production only)"
  cat "$OUTPUT_DIR/log_placement_analyzer.log" | grep 'methods.*logged.*ratio'
}

BASEDIR="$(pwd)"
PYSCRIPTS="$BASEDIR/tools"
TOOLSDIR="$BASEDIR/tools/bin"

OUTPUT_DIR="$BASEDIR/out/analysis/$(basename "$PROJECT_PATH")"

# Always save in a clean state for the current project
[[ -d "$OUTPUT_DIR" ]] && rm -rf "$OUTPUT_DIR"
mkdir -p "$OUTPUT_DIR"

# Experiment pipeline
{
  pushd "$PROJECT_PATH" || exit 1
    check_rev_precondition
    find_java_sources
    analyze_log_statements
  popd || exit 1
} | tee "$OUTPUT_DIR/pipeline-analysis.log"
