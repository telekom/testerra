import {Aurelia} from 'aurelia-framework';
import * as environment from '../config/environment.json';
import {PLATFORM} from 'aurelia-pal';

export function configure(aurelia: Aurelia) {
  aurelia.use
    .standardConfiguration()
    .plugin(PLATFORM.moduleName('@aurelia-mdc-web/all'))
    .globalResources([
      PLATFORM.moduleName('components/apex-chart/apex-chart'),
      PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/date-format-value-converter'),
    ])

  aurelia.use.developmentLogging(environment.debug ? 'debug' : 'warn');

  aurelia.start().then(() => aurelia.setRoot(PLATFORM.moduleName('app')));
}
