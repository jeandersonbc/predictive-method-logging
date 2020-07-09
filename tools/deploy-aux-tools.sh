#!/usr/bin/env bash
# TODO should be integrated into gradle build
rm -rf "tools/lib" "tools/bin"
for f in 'log-remover' 'log-placement-analyzer'; do
    tar xf "$(find . -type f -name "$f.tar")" -C tools --strip 1
done

pushd log-prediction || exit 1
pip3 install .
popd
