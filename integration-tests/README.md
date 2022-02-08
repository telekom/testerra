# Integration tests

## Run pretest
```shell
gradle test -Ppretest -Dlog4j.configurationFile=src/test/resources/log4j-pretest.xml
```

## Start Report prepare tests (deprecated)

Generate the demo reports for report tests

```bash
./generate-reports.sh
```

## Start complete integration testset

```shell script
gradle test
````

## Start a predefined suite

Start the smoke test
```shell script
gradle test -P smoke=true
````
