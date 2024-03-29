#!/usr/bin/env bash
DOWNLOAD_DIR="$(pwd)/apache-download"
APACHE_PROJECTS=apache-projects
INPUT_CSV="$1"

[[ ! -d "$DOWNLOAD_DIR" ]] &&
  mkdir -p "$DOWNLOAD_DIR"

rm -rf "$APACHE_PROJECTS" &&
  mkdir "$APACHE_PROJECTS"

sed 's/,/ /' "$INPUT_CSV" |
  while read -r url rev; do

    name="$(echo $url | sed 's/.*\///' | sed 's/\.git//')"
    output="$DOWNLOAD_DIR/$name"
    output_double_check="$APACHE_PROJECTS/$name.sh"

    # If not downloaded yet, clone it
    if [[ ! -d "$output" ]]; then
      git clone "$url" "$output" && {
        pushd "$output"
        git reset --hard $rev
        popd
      }
    fi

    # Experiments don't read user-informed paths. Instead, they read this
    # output_double_check file that exports the abspath to the project and the
    # expected revision based on the input CSV.
    {
      echo "#!/usr/bin/env bash"
      echo "PROJECT_PATH=\"\$(pwd)/$(basename "$DOWNLOAD_DIR")/$name\""
      echo "REV=$rev"
    } > "$output_double_check"

  done
