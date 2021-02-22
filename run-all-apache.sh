#!/usr/bin/env bash
BASEDIR="$(pwd)"
TOOLS_DIR="$BASEDIR/tools"
APACHE_HOME="$BASEDIR/apache-projects"
SELECTION_DIR="$BASEDIR/out/selection"

analysis() {
  echo Running ANALYSIS step
  if [[ ! -d "$APACHE_HOME" ]]; then
    echo Missing APACHE_HOME dir. Did you run apache_download.sh?
    echo Aborting
    exit 1
  fi
  find "$APACHE_HOME" -type f -path '*.sh' |
    while read -r subject; do
      time "$TOOLS_DIR/pipeline-analysis.sh" "$subject"
    done
  echo Done ANALYSIS step
}

selection() {
  echo Running selection pipeline
  local OUTPUT_DATA="$SELECTION_DIR/data"

  if [[ ! -d "$APACHE_HOME" ]]; then
    echo Missing APACHE_HOME dir. Did you run apache_download.sh?
    echo Aborting
    exit 1
  fi

  # Always save in a clean state
  [[ -d "$SELECTION_DIR" ]] && rm -rf "$SELECTION_DIR"
  mkdir -p "$OUTPUT_DATA"

  "$TOOLS_DIR"/summary-analysis.sh >"$OUTPUT_DATA/analysis.csv"
  "$TOOLS_DIR"/selection.R "$OUTPUT_DATA/analysis.csv" | tee "$OUTPUT_DATA/selection.log"
  mv filtering.csv "$OUTPUT_DATA"

  grep -v '"name","java_files' "$OUTPUT_DATA/filtering.csv" |
    sed 's/",.*//' | sed 's/"//' |
    while read -r name; do
      cp "$APACHE_HOME/$name.sh" "$SELECTION_DIR"
    done
}

log_removal() {
  echo Running LOG-REMOVAL step
  if [[ ! -d "$SELECTION_DIR" ]]; then
    echo Missing SELECTION dir. Did you run the run-selection script?
    echo Aborting
    exit 1
  fi

  find "$SELECTION_DIR" -type f -path '*.sh' |
    while read -r subject; do
      echo Running "$subject"
      time "$TOOLS_DIR/pipeline-log-removal.sh" "$subject"
      echo "done $subject"
    done
  echo Done LOG-REMOVAL step
}

code_metrics() {
  echo Running CODE-METRICS step
  if [[ ! -d "$SELECTION_DIR" ]]; then
    echo Missing SELECTION dir. Did you run the run-selection script?
    echo Aborting
    exit 1
  fi

  find "$SELECTION_DIR" -type f -path '*.sh' |
    while read -r subject; do
      echo Running "$subject"
      time "$TOOLS_DIR/pipeline-codemetrics.sh" "$subject"
    done
  echo Done CODE-METRICS step
}

create_dataset() {
  echo Running DATASET step
  if [[ ! -d "$SELECTION_DIR" ]]; then
    echo Missing SELECTION dir. Did you run the run-selection script?
    echo Aborting
    exit 1
  fi

  find "$SELECTION_DIR" -type f -path '*.sh' |
    while read -r subject; do
      echo Running "$subject"
      time "$TOOLS_DIR/build-datasets.sh" "$subject"
    done
  echo Done DATASET step
}

extract_textual_features() {
  echo Running TEXTUAL FEATURES step
  if [[ ! -d "$SELECTION_DIR" ]]; then
    echo Missing SELECTION dir. Did you run the run-selection script?
    echo Aborting
    exit 1
  fi

  find "$SELECTION_DIR" -type f -path '*.sh' |
    while read -r subject; do
      echo Running "$subject"
      time "$TOOLS_DIR/pipeline-textual-features.sh" "$subject"
    done
  echo Done TEXTUAL FEATURES step
}

# main
time analysis
time selection
time log_removal
time code_metrics
time create_dataset
time extract_textual_features
