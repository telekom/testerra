# report
The `report` module will use Testerra Hooks, Listener and Events to receive data from Testerra `core` module and generate a simple HTML report.

## Installation / Usage

For maven:

```xml
<dependencies>
    <dependency>
        <groupId>eu.tsystems.mms.tic.testerra</groupId>
        <artifactId>report</artifactId>
        <version>1-SNAPSHOT</version>
    </dependecy>
</dependencies>
```

For gradle:
```text
compile 'eu.tsystems.mms.tic.testerra:report:1-SNAPSHOT'
```

## Run 'Report under Test' tests

```shell
gradle test -P nr=1
```

Or run all
```shell
./generate-report.sh
```
