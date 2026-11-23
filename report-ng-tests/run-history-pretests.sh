#!/usr/bin/env bash
# Runs the report history pretests 11 times to generate the history report.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${ROOT_DIR}"

for i in $(seq 1 11); do
    echo "=== Running history pretest ${i}/11 ==="
    # Failing tests are expected in the pretests, so the run continues on errors
    ./gradlew :report-ng-tests:test -PpretestHistoryIndex="${i}" || true
done
