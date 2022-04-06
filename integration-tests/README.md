# Integration tests

## Run pretest
```shell
gradle test -P pretest=true -D log4j2.configurationFile=src/test/resources/log4j2-pretest.xml
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
