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
import {IntlDateFormatValueConverter} from "t-systems-aurelia-components/src/value-converters/intl-date-format-value-converter"
import {ObjectStorage} from "t-systems-aurelia-components/src/utils/object-storage";

export function configure(aurelia: Aurelia) {

    const config:Config = aurelia.container.get(Config);

    aurelia.use
        .standardConfiguration()
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/base'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/card'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/chips'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/checkbox'))
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
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/radio'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/ripple'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/select'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/snackbar'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/switch'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/text-field'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/tab-bar'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/tooltip'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/top-app-bar'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/typography'))
        .plugin(PLATFORM.moduleName('@aurelia-mdc-web/lookup'))
        .plugin(PLATFORM.moduleName('aurelia-ui-virtualization'))
        .globalResources([
            PLATFORM.moduleName('components/apex-chart/apex-chart'),
            PLATFORM.moduleName('components/class-name-markup/class-name-markup'),
            PLATFORM.moduleName('components/method-tags/method-tags'),
            PLATFORM.moduleName('components/log-view/log-view'),
            PLATFORM.moduleName('components/log-view/virtual-log-view'),
            PLATFORM.moduleName('components/code-view/code-view'),
            PLATFORM.moduleName('components/alert/alert.html'),
            PLATFORM.moduleName('components/lazy-image/lazy-image'),
            PLATFORM.moduleName('components/lazy-video/lazy-video'),
            PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/object-values-value-converter'),
            PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/highlight-text-value-converter'),
            PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/duration-format-value-converter'),
            PLATFORM.moduleName('t-systems-aurelia-components/src/value-converters/intl-date-format-value-converter'),
            PLATFORM.moduleName('t-systems-aurelia-components/src/components/echart/echart'),
            PLATFORM.moduleName('value-converters/json-value-converter'),
            PLATFORM.moduleName('value-converters/status-icon-name-value-converter'),
            PLATFORM.moduleName('value-converters/status-name-value-converter'),
            PLATFORM.moduleName('value-converters/status-class-value-converter'),
            PLATFORM.moduleName('value-converters/html-value-converter'),
            PLATFORM.moduleName('value-converters/log-level-value-converter'),
            PLATFORM.moduleName('value-converters/html-escape-value-converter'),
            PLATFORM.moduleName('value-converters/class-name-value-converter'),
            /**
             * This is super important to dialogs
             * https://discourse.aurelia.io/t/solved-error-cannot-determine-default-view-strategy-for-object/3589/2
             */
            PLATFORM.moduleName('components/screenshot-comparison/screenshot-comparison'),
            PLATFORM.moduleName('components/screenshots-dialog/screenshots-dialog'),
        ]);

    if (config.developmentMode) {
        aurelia.use.developmentLogging("debug");
    }

    // Setup defaults
    const formatter = aurelia.container.get(IntlDateFormatValueConverter);
    formatter.setLocale('en-GB');
    formatter.setOptions('default', { weekday: 'short', year: 'numeric', month: 'short', day: 'numeric',  hour: 'numeric', minute: 'numeric', second: 'numeric', hour12: false });
    formatter.setOptions('long', { dateStyle: 'full', timeStyle: 'long' });
    formatter.setOptions( 'log', {hour: 'numeric', minute: 'numeric', second: 'numeric', fractionalSecondDigits: 3, hour12: false})
    formatter.setOptions('step', { year: 'numeric', month: 'short', day: 'numeric',  hour: 'numeric', minute: 'numeric', second: 'numeric', hour12: false });
    formatter.setOptions('date', {year: 'numeric', month: 'short', day: 'numeric'});
    formatter.setOptions('time', {hour: 'numeric', minute: 'numeric', second: 'numeric', hour12: false});
    formatter.setOptions('full', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric',
        second: 'numeric',
        hour12: false
    });
    formatter.setOptions('print', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false,
    });

    const objectStorage = aurelia.container.get(ObjectStorage);
    objectStorage.setStorage(localStorage);

    aurelia.start().then(() => aurelia.setRoot(PLATFORM.moduleName('app')));
}
