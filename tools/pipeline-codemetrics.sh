#!/usr/bin/env bash

# Loads subject settings from an external bash script if informed
# shellcheck source=/dev/null
[[ -n "$1" ]] && . "$1"
PROJECT_NAME="$(basename $PROJECT_PATH)"

BASEDIR="$(pwd)"
TOOLSDIR="$BASEDIR/tools"
LOG_REMOVAL_BASEDIR="$BASEDIR/out/log-removal/$PROJECT_NAME"
ANALYSIS_DIR="$BASEDIR/out/analysis/$PROJECT_NAME"

run_ck() {
    local project_path=$1
    local output_dir=$2

    echo "Running CK"
    echo "input=$project_path"
    echo "output=$output_dir"

    rm -rf $output_dir && mkdir -p $output_dir

    local ck_jar="$TOOLSDIR/ck-0.6.3-SNAPSHOT-jar-with-dependencies.jar"
    local args="-Xms14g -Dlog4j.configuration=file:$TOOLSDIR/log4j.xml"
    java $args -jar "$ck_jar" "$project_path" false 100 false &> $output_dir/ck.log

    mv class.csv method.csv $output_dir
    echo Done
}

COPIED_SOURCES="$LOG_REMOVAL_BASEDIR/copied"
CK_OUTPUT_COPIED="$BASEDIR/out/codemetrics/$PROJECT_NAME/copied"
run_ck "$COPIED_SOURCES" "$CK_OUTPUT_COPIED"

NOLOG_SOURCES="$LOG_REMOVAL_BASEDIR/nolog"
CK_OUTPUT_NOLOG="$BASEDIR/out/codemetrics/$PROJECT_NAME/nolog"
run_ck "$NOLOG_SOURCES" "$CK_OUTPUT_NOLOG"
{
  echo Known Issues:
  python3 "$TOOLSDIR/sanity_check.py" \
          "$CK_OUTPUT_COPIED/method.csv" \
          "$CK_OUTPUT_NOLOG/method.csv" \
          "$ANALYSIS_DIR/log-placement.csv"
  tail -n 1 "$ANALYSIS_DIR/log_placement_analyzer.log"

} | tee "$BASEDIR/out/codemetrics/$PROJECT_NAME/known-issue.txt"
