#!/usr/bin/env bash
distTar() {
  local tar_name=$1
  local dir_name="${tar_name//.tar/}"
  ./gradlew clean distTar
  tar xvf "build/distributions/$tar_name" &&
    mv -f "$dir_name"/bin/* "$BINS" &&
    mv -f "$dir_name"/lib/* "$LIBS" &&
    rm -rf "$dir_name"
}

if [ ! -d 'log-removal' ]; then
  echo Missing log-removal dir
  echo Aborting
  exit 1
fi
if [ ! -d 'log-placement-analyzer' ]; then
  echo Missing log-placement-analyzer dir
  echo Aborting
  exit 1
fi

BASEDIR="$(pwd)"
TOOLS_DIR="$BASEDIR/tools"
BINS="$TOOLS_DIR/bin"
LIBS="$TOOLS_DIR/lib"

echo Cleaning up BIN and LIB dirs
rm -rf "$BINS" "$LIBS"
mkdir -p "$BINS" "$LIBS"

pushd log-removal || exit 1
distTar log-removal.tar
popd || exit 1

pushd log-placement-analyzer || exit 1
distTar log-placement-analyzer.tar
popd || exit 1
