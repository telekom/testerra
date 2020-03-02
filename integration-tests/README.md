# Integration tests

## Start Report prepare tests

Generate the demo Testerra reports for report tests

```shell script
for i in {1..9}
do
  gradle test -P nr=$(i) -Dtt.report.name=pretest0$(i)

done
````

## Start complete integration testset

```shell script
gradle test
````

## Start a predefined suite

Start the smoke test
```shell script
gradle test -P smoke=true
````
