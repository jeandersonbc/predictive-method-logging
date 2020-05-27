#!/usr/bin/env bash
echo 'subject,methods,logged_methods,ratio,log_stmts'
find out -type f -name 'known-issue.txt' |
  while read -r p; do
    name="$(echo "$p" | awk -F/ '{print $3}')"

    cat $p |
      grep -v 'Error measurement' |
      sed 's/[a-z_]*=//g' |
      sed 's/ /,/g' |
      sed "s/^/$name-/"

    echo ""
  done;

