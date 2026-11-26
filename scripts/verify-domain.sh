#!/usr/bin/env sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
if ! command -v kotlinc >/dev/null 2>&1; then
  echo "kotlinc is required to run this standalone domain check." >&2
  exit 127
fi
TMP_DIR=$(mktemp -d "${TMPDIR:-/tmp}/amaraty-domain.XXXXXX")
trap 'rm -rf "$TMP_DIR"' EXIT HUP INT TERM
find "$ROOT/core/domain/src/main/kotlin" -name '*.kt' -print > "$TMP_DIR/sources.txt"
kotlinc $(cat "$TMP_DIR/sources.txt") -d "$TMP_DIR/domain.jar"
kotlinc -classpath "$TMP_DIR/domain.jar" "$ROOT/scripts/DomainSmoke.kt" -include-runtime -d "$TMP_DIR/domain-smoke.jar"
java -cp "$TMP_DIR/domain-smoke.jar:$TMP_DIR/domain.jar" DomainSmokeKt
