#!/usr/bin/env bash

# Loads subject settings from an external bash script if informed
# shellcheck source=/dev/null
[[ -n "$1" ]] && . "$1"

# Subject settings
if [ -z "$PROJECT_PATH" ]; then
  echo Missing PROJECT_PATH variable >&2
  echo Aborted. >&2
  exit 1
fi

BASEDIR="$(pwd)"
PROJECT_NAME="$(basename "$PROJECT_PATH")"

DATASET_CSV="$BASEDIR/out/dataset/$PROJECT_NAME/dataset_full.csv"
if [ ! -f "$DATASET_CSV" ]; then
  echo Missing DATASET CSV \""$DATASET_CSV"\" >&2
  exit 1
fi

OUTPUT_BASE="$BASEDIR/out/ml/$PROJECT_NAME"

run_ml_experiments() {
  for model in "dt" "rf" "et" "ab" "lr"; do
    local output_dir="$OUTPUT_BASE/$model"
    rm -rf "$output_dir" && mkdir -p "$output_dir"
    echo $PROJECT_NAME: Running model $model
    pushd "$output_dir" || exit 1
    time python3 -m logpred_method "$model" "$DATASET_CSV" | tee logpred-method.log
    popd || exit 1

    for balancing in "smote" "rus"; do
        output_dir="$OUTPUT_BASE/$model-$balancing"
        rm -rf "$output_dir" && mkdir -p "$output_dir"
        echo $PROJECT_NAME: Running balancing option $balancing
        pushd "$output_dir" || exit 1
        time python3 -m logpred_method "$model" "$DATASET_CSV" --balancing "$balancing" | tee logpred-method.log
        popd || exit 1
    done
  done
}

time run_ml_experiments
