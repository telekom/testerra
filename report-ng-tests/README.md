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
```shell
for i in `seq 1 10`; do
    gradle test -PpretestHistoryIndex=${'$'}i -Dtt.report.dir=/src/test/resources/reports/report-history -Dtt.report.name=PreTest_History
done
```

## Start Test of generated Testerra Report
UI-Test of formerly generated Testerra Report

```shell script
gradle test
````
