#!/usr/bin/env bash
for i in {1..9}
do
  gradle test -P nr=$i -Dtt.report.name=pretest0$i

done
