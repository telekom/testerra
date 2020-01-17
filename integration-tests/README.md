# Integration tests

## Report tests

```shell script
for i in {1..9}
do
  gradle test -P nr=$(i) -Dtt.report.name=pretest0$(i)

done
