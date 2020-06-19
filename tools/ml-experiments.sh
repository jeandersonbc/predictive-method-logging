#!/usr/bin/env bash

BASEDIR="$(pwd)"
PROJECT_NAME="$1"

DATASET_CSV="$BASEDIR/out/dataset/$PROJECT_NAME/dataset_full.csv"
if [ ! -f "$DATASET_CSV" ]; then
  echo Missing DATASET CSV \""$DATASET_CSV"\" >&2
  exit 1
fi


run_standard_experiments() {
  local output_base="$BASEDIR/out/ml/$PROJECT_NAME"

  for model in "dt" "rf" "et" "ab" "lr"; do

    local output_dir="$output_base/$model"
    rm -rf "$output_dir" && mkdir -p "$output_dir"

    echo $PROJECT_NAME: Running model $model
    pushd "$output_dir" || exit 1
    time python3 -m logpred_method "$model" "$DATASET_CSV" | tee logpred-method.log
    popd || exit 1

    for balancing in "smote" "rus"; do
      output_dir="$output_base/$model-$balancing"

      rm -rf "$output_dir" && mkdir -p "$output_dir"

      echo $PROJECT_NAME: Running balancing option $balancing
      pushd "$output_dir" || exit 1
      time python3 -m logpred_method "$model" "$DATASET_CSV" --balancing "$balancing" | tee logpred-method.log
      popd || exit 1
    done
  done
}

time run_standard_experiments
