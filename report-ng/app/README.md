# Report NG

## Development mode
```bash
npm start
```

## Compile Proto
Generates models into `src/services/report-model/*.js`
```bash
npm run generateProto
```

## Build release
Generates the application into `../src/main/resources/report-ng`
```bash
npm run build
```

## Analyze package sizes
Generates visualization of package sizes
```bash
npm run analyze
```

## Update aurelia-mdc

```shell
npm install -g npm-check-updates
ncu -u "/aurelia-mdc-web/"
npm install
```

## References

- Material Framework: https://github.com/aurelia-ui-toolkits/aurelia-mdc-web
- Charts Framework: 
  - https://apexcharts.com/
  - https://echarts.apache.org/
  - https://visjs.github.io/vis-network/docs/network/
- Time and Date: https://momentjs.com/
- Duration Format: https://github.com/jsmreese/moment-duration-format
- Aurelia Value Converters: https://aurelia.io/docs/binding/value-converters#converter-parameters
