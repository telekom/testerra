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
 *
 */
package eu.tsystems.mms.tic.testframework.l10n;

import eu.tsystems.mms.tic.testframework.common.UTF8ResourceBundleControl;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizedBundle {
    private Locale locale;
    private ResourceBundle currentResourceBundle;
    private final String bundleName;


    public LocalizedBundle(String bundleName) {
        this.bundleName = bundleName;
        updateResourceBundle();
    }

    private ResourceBundle getResourceBundle() {
        if (Locale.getDefault() != locale) {
            updateResourceBundle();
        }
        return currentResourceBundle;
    }

    private void updateResourceBundle() {
        currentResourceBundle = ResourceBundle.getBundle(bundleName, new UTF8ResourceBundleControl());
        locale = Locale.getDefault();
    }

    public String getString(final String label) {
        ResourceBundle resourceBundle = getResourceBundle();
        if (resourceBundle.containsKey(label)) {
            return resourceBundle.getString(label);
        } else {
            return label;
        }
    }
}
