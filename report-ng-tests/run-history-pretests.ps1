# Runs the report history pretests 11 times to generate the history report.
$ErrorActionPreference = 'Stop'

$rootDir = Resolve-Path (Join-Path $PSScriptRoot '..')

Push-Location $rootDir
try {
    for ($i = 1; $i -le 11; $i++) {
        Write-Host "=== Running history pretest $i/11 ==="
        # Failing tests are expected in the pretests, so the run continues on errors
        & .\gradlew.bat :report-ng-tests:test "-PpretestHistoryIndex=$i"
    }
}
finally {
    Pop-Location
}
