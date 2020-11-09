import {Aurelia} from 'aurelia-framework';
import {PLATFORM} from 'aurelia-pal';

export function configure(aurelia: Aurelia) {
    aurelia.use
        .standardConfiguration()
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/all'))
        .globalResources([
            PLATFORM.moduleName('components/apex-chart/apex-chart'),
            PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/date-format-value-converter'),
            PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/highlight-text-value-converter'),
            PLATFORM.moduleName('value-converters/status-icon-name-value-converter'),
            PLATFORM.moduleName('value-converters/status-name-value-converter'),
            PLATFORM.moduleName('value-converters/status-class-value-converter'),
            PLATFORM.moduleName('value-converters/duration-format-value-converter'),
            PLATFORM.moduleName('value-converters/html-value-converter'),
        ])

    aurelia.use.developmentLogging("debug");

    aurelia.start().then(() => aurelia.setRoot(PLATFORM.moduleName('app')));
}
