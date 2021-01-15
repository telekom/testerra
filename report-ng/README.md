# Report NG

## Build

Cleans also the resources directory
```shell
gradle clean
```

Cleans the application build environment (including `node_modules`)

```shell
gradle npmClean
```

Builds the Report NG from `app` and copies the files to `src/main/resources`
```shell
gradle buildReport
```
