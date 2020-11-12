import {Aurelia} from 'aurelia-framework';
import {PLATFORM} from 'aurelia-pal';
import {Config} from "./services/config";

export function configure(aurelia: Aurelia) {

    const config:Config = aurelia.container.get(Config);

    aurelia.use
        .standardConfiguration()
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/all'))
        .globalResources([
            PLATFORM.moduleName('components/apex-chart/apex-chart'),
            PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/date-format-value-converter'),
            PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/highlight-text-value-converter'),
            PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/duration-format-value-converter'),
            PLATFORM.moduleName('value-converters/status-icon-name-value-converter'),
            PLATFORM.moduleName('value-converters/status-name-value-converter'),
            PLATFORM.moduleName('value-converters/status-class-value-converter'),
            PLATFORM.moduleName('value-converters/html-value-converter'),
        ]);

    if (config.developmentMode) {
        aurelia.use.developmentLogging("debug");
    }

    aurelia.start().then(() => aurelia.setRoot(PLATFORM.moduleName('app')));
}
