#!/usr/bin/env bash

# Loads subject settings from an external bash script if informed
# shellcheck source=/dev/null
[[ -n "$1" ]] && . "$1"
PROJECT_NAME="$(basename "$PROJECT_PATH")"

BASEDIR="$(pwd)"
TOOLSDIR="$BASEDIR/tools"
LOG_REMOVAL_BASEDIR="$BASEDIR/out/log-removal/$PROJECT_NAME"

run_ck() {
    local input_path=$1
    local output_dir=$2

    echo "input=$input_path"
    echo "output=$output_dir"

    # Rerunning CK can be costly... let's just reuse if metrics were already computed
    if [[ -f "$output_dir/method.csv" ]] &&
       [[ -f "$output_dir/class.csv" ]] &&
       [[ -f "$output_dir/ck.log" ]]; then
        #echo "Found execution files, skipping CK run"
        #return 0
        echo "Overwritting stuff..."
    fi

    echo "Running CK"
    rm -rf "$output_dir" && mkdir -p "$output_dir"

    local ck_jar="$TOOLSDIR/ck-0.6.3-SNAPSHOT-jar-with-dependencies.jar"
    local args="-Xms14g -Dlog4j.configuration=file:$TOOLSDIR/log4j.xml"

    # intended behavior
    # shellcheck disable=SC2086
    java $args -jar "$ck_jar" "$input_path" false 100 false &> $output_dir/ck.log

    mv class.csv method.csv "$output_dir"
    echo Done
}

COPIED_SOURCES="$LOG_REMOVAL_BASEDIR/copied"
CK_OUTPUT_COPIED="$BASEDIR/out/codemetrics/$PROJECT_NAME/copied"
run_ck "$COPIED_SOURCES" "$CK_OUTPUT_COPIED"

NOLOG_SOURCES="$LOG_REMOVAL_BASEDIR/nolog"
CK_OUTPUT_NOLOG="$BASEDIR/out/codemetrics/$PROJECT_NAME/nolog"
run_ck "$NOLOG_SOURCES" "$CK_OUTPUT_NOLOG"

