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

Builds the Report NG from `app-react` and copies the files to `src/main/resources`.

```shell
gradle buildReport
```

## Testing

`build` and `test` in module `report-ng` do not trigger `buildReport` automatically.
For `integration-tests` and `report-ng-tests`, `test` runs `buildReport` before and refresh the resources.
