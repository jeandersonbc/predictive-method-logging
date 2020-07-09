#!/usr/bin/env bash
BASEDIR="$(pwd)"
TOOLS_DIR="$BASEDIR/tools"
SELECTION_DIR="$BASEDIR/out/selection"
DATASET_DIR="$BASEDIR/out/dataset"

SUBJECTS=$(
  find "$SELECTION_DIR" -type f -path '*.sh' -exec basename {} \; |
    sed 's/.sh//'
)

OUTPUT_DIR="$BASEDIR/out/dataset/merge-apache"
[[ ! -d "$OUTPUT_DIR" ]] && mkdir -p "$OUTPUT_DIR"

pushd "$OUTPUT_DIR"
python3 $TOOLS_DIR/merge-apache-csv.py $DATASET_DIR $SUBJECTS
popd

$TOOLS_DIR/ml-experiments.sh merge-apache
