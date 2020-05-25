#!/usr/bin/env bash

BASEDIR="$(pwd)"
TARGET_DIR="$BASEDIR/out/analysis"

echo name,java_files,production,test,doc,build,log_stmts,methods,logged_methods,ratio
{
  find "$TARGET_DIR" -type f -name 'pipeline-analysis.log' |
    while read -r LOG_FILE; do
      NAME=${LOG_FILE//*analysis\//}
      NAME=${NAME//\/*/}

      JAVA_FILES="$(grep 'java files' "$LOG_FILE" | awk '{print $2}')"
      PROD="$(grep ' production-related' "$LOG_FILE" | awk '{print $1}')"
      TEST="$(grep 'test-related' "$LOG_FILE" | awk '{print $1}')"
      DOC="$(grep 'doc-related' "$LOG_FILE" | awk '{print $1}')"
      BUILD="$(grep 'build-related' "$LOG_FILE" | awk '{print $1}')"
      LOG_STATEMENTS="$(grep ' log statements' "$LOG_FILE" | awk '{print $1}')"
      METHOD_INFO=$(grep 'methods.*logged.*' "$LOG_FILE" | awk '{print $2","$4","$6}')

      echo "$NAME,$JAVA_FILES,$PROD,$TEST,$DOC,$BUILD,$LOG_STATEMENTS,$METHOD_INFO"

    done
} | sort --field-separator="," -rnk 10