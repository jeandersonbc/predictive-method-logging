#!/usr/bin/env bash
#
# Runs log-removal pipeline on subject.
# Note: Skips file copy if already done
#       Skips log-removal if already done
#
# Pipeline:
#   1. Pre-condition check: repo must be at specific revision
#   2. Copy production-related files to output_dir
#   3. Check if there are the expected amount of java files
#   4. Run log-removal
#
# -Enjoy it!

# Loads subject settings from an external bash script if informed
# shellcheck source=/dev/null
[[ -n "$1" ]] && . "$1"

# Subject settings
[[ -z "$PROJECT_PATH" ]] &&
  echo Missing PROJECT_PATH variable &&
  echo Aborted. &&
  exit 1
[[ -z "$REV" ]] &&
  echo Missing REV variable &&
  echo Aborted. &&
  exit 1

check_rev_precondition() {
  local current=$(git rev-parse HEAD)
  if [ ! "$REV" == "$current" ]; then
    echo check_rev_rule: Expected "$REV" but was "$current"
    echo Aborted
    exit 1
  fi
}

copy_files() {
  echo Copying selected sources
  grep 'production-related' "$SOURCE_CLASSIFICATION" |
    awk '{print $1}' |
    while read -r src; do
      rsync -avR "$src" "$COPIED_SOURCES/" 1>/dev/null
    done
}

check_expected_files() {
  local existing_files_cnt=$(find $COPIED_SOURCES -name '*.java' | wc -l)
  local expected_files_cnt=$(cat $SOURCE_CLASSIFICATION | grep 'production-related' | wc -l)
  if [ ! "$existing_files_cnt" == "$expected_files_cnt" ]; then
    echo Inconsistent counting
    echo expected:$expected_files_cnt actual:$existing_files_cnt
    echo Aborted.
    exit 1
  fi
}

BASEDIR="$(pwd)"
BINS="$BASEDIR/tools/bin"
PROJECT_NAME="$(basename "$PROJECT_PATH")"

SOURCE_CLASSIFICATION="$BASEDIR/out/analysis/$PROJECT_NAME/java_sources_classified.txt"
if [ ! -f "$SOURCE_CLASSIFICATION" ]; then
  echo Missing java classification file
  echo Aborted.
  exit 1
fi

# Working on a clean state
OUTPUT_DIR="$BASEDIR/out/log-removal/$PROJECT_NAME"
rm -rf "$OUTPUT_DIR"
mkdir -p "$OUTPUT_DIR"

COPIED_SOURCES="$OUTPUT_DIR/copied"
NOLOGS_SOURCES="$OUTPUT_DIR/nolog"
OUTPUT_LOG="$OUTPUT_DIR/log-removal.log"

# Experiment pipeline
pushd "$PROJECT_PATH" || exit 1

check_rev_precondition
time copy_files
check_expected_files

[[ -d "$NOLOGS_SOURCES" ]] && rm -rf "$NOLOGS_SOURCES"
mkdir -p "$NOLOGS_SOURCES"
cp -fr "$COPIED_SOURCES/" "$NOLOGS_SOURCES/"

popd || exit 1

echo Running log-removal
time $BINS/log-remover "$NOLOGS_SOURCES" 1>"$OUTPUT_LOG"
