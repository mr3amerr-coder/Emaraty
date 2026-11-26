#!/usr/bin/env sh
# Bootstrap shim for this offline-generated project.
# Replace with the official Gradle Wrapper by running:
#   gradle wrapper --gradle-version 9.9.2 --distribution-type bin
set -eu
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi
printf '%s\n' \
  'Gradle 9.9.2 is not installed and the official wrapper JAR could not be bundled in the offline build environment.' \
  'Install Gradle 9.9.2, then run: gradle wrapper --gradle-version 9.9.2 --distribution-type bin' >&2
exit 127
