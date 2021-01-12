/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {Aurelia} from 'aurelia-framework';
import {PLATFORM} from 'aurelia-pal';
import {Config} from "./services/config-dev";
import {DateFormatValueConverter} from "t-systems-aurelia-components/src/value-converters/date-format-value-converter";

export function configure(aurelia: Aurelia) {

    const config:Config = aurelia.container.get(Config);

    aurelia.use
        .standardConfiguration()
        //.plugin(PLATFORM.moduleName('@aurelia-mdc-web/button'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/card'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/data-table'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/dialog'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/drawer'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/elevation'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/expandable'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/form-field'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/icon-button'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/layout-grid'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/linear-progress'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/list'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/lookup'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/ripple'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/select'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/switch'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/text-field'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/tab-bar'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/top-app-bar'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/typography'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/lookup'))
        .globalResources([
            PLATFORM.moduleName('components/apex-chart/apex-chart'),
            PLATFORM.moduleName('components/class-name/class-name'),
            PLATFORM.moduleName('components/method-tags/method-tags'),
            PLATFORM.moduleName('components/log-view/log-view'),
            PLATFORM.moduleName('components/code-view/code-view'),
            PLATFORM.moduleName('components/alert/alert.html'),
            PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/date-format-value-converter'),
            PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/object-values-value-converter'),
            PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/highlight-text-value-converter'),
            PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/duration-format-value-converter'),
            //PLATFORM.moduleName('t-systems-aurelia-components/src/attributes/become-visible-custom-attribute'),
            PLATFORM.moduleName('value-converters/status-icon-name-value-converter'),
            PLATFORM.moduleName('value-converters/status-name-value-converter'),
            PLATFORM.moduleName('value-converters/status-class-value-converter'),
            PLATFORM.moduleName('value-converters/html-value-converter'),
            PLATFORM.moduleName('value-converters/log-level-value-converter'),
        ]);

    if (config.developmentMode) {
        aurelia.use.developmentLogging("debug");
    }

    DateFormatValueConverter.setDefaultFormat("ddd MMM D HH:mm:ss ZZ YYYY");

    aurelia.start().then(() => aurelia.setRoot(PLATFORM.moduleName('app')));
}
