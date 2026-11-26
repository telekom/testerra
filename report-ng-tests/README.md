# ReportNG tests

## Run pretest
Generate Testerra Report with several status

```shell
gradle test -P pretestRNG=true
```

## Run additional pretest (separate test set)
Generate Testerra Report with additional status

```shell
gradle test -P pretestRNGExtended=true
```

## Run report history pretests
Runs the history pretests 11 times (`-PpretestHistoryIndex=1..11`).

Linux / macOS:
```shell
./report-ng-tests/run-history-pretests.sh
```

Windows (PowerShell):
```powershell
.\report-ng-tests\run-history-pretests.ps1
```

## Start Test of generated Testerra Report
UI-Test of formerly generated Testerra Report

```shell script
gradle test
````
