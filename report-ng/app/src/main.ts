import {Aurelia} from 'aurelia-framework';
import * as environment from '../config/environment.json';
import {PLATFORM} from 'aurelia-pal';

export function configure(aurelia: Aurelia) {
  aurelia.use
    .standardConfiguration()
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/base'))
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/button'))
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/data-table'))
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/dialog'))
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/elevation'))
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/drawer'))
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/switch'))
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/form-field'))
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/ripple'))
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/menu'))
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/radio'))
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/tab-bar'))
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/top-app-bar'))
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/text-field'))
    .globalResources([
      PLATFORM.moduleName('components/apex-chart/apex-chart'),
      PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/date-format-value-converter'),
    ])

  aurelia.use.developmentLogging(environment.debug ? 'debug' : 'warn');

  aurelia.start().then(() => aurelia.setRoot(PLATFORM.moduleName('app')));
}
