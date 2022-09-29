# ReportNG tests

## Run pretest
Generate Testerra Report with several status

```shell
gradle test -P pretest=true
```


## Run additional pretest (separate test set)
Generate Testerra Report with additional status

```shell
gradle test -P pretestExtended=true
```

## Start Test of generated Testerra Report
UI-Test of formerly generated Testerra Report

```shell script
gradle test
````

Or run all
```shell
./generate-report.sh
```
