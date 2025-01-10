/*
 * Testerra
 *
 * (C) 2025, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {data} from "../services/report-model";

export class MethodTagValueConverter {
    toView(value: data.MethodType) {
        switch (value) {
            case data.MethodType.TEST_METHOD: return "Test";
            case data.MethodType.CONFIGURATION_BEFORE_SUITE: return "beforeSuite";
            case data.MethodType.CONFIGURATION_BEFORE_TEST: return "beforeTest";
            case data.MethodType.CONFIGURATION_BEFORE_CLASS: return "beforeClass";
            case data.MethodType.CONFIGURATION_BEFORE_METHOD: return "beforeMethod";
            case data.MethodType.CONFIGURATION_BEFORE_GROUPS: return "beforeGroups";
            case data.MethodType.CONFIGURATION_AFTER_GROUPS: return "afterGroups";
            case data.MethodType.CONFIGURATION_AFTER_METHOD: return "afterMethod";
            case data.MethodType.CONFIGURATION_AFTER_CLASS: return "afterClass";
            case data.MethodType.CONFIGURATION_AFTER_TEST: return "afterTest";
            case data.MethodType.CONFIGURATION_AFTER_SUITE: return "afterSuite";
            case data.MethodType.DATA_PROVIDER: return "Data provider";
            case data.MethodType.CONFIGURATION_METHOD: return "Configuration"
            default: return "UNKNOWN";
        }
    }
}
