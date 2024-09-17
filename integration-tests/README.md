# Integration tests

## Run pretest
```shell
# The pretest to check test status
gradle test -P pretest=true
```

```shell
# The pretest to check dry run
gradle test -P pretestDryRun=true
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
