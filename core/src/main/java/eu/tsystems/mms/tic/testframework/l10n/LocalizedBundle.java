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
    private boolean useDefaultLocale;
    private ResourceBundle currentResourceBundle;
    private final String bundleName;

    /**
     * Initialize the bundle with default locale and reloads the internal
     * resource bundle when the default locale changes.
     */
    public LocalizedBundle(String bundleName) {
        this(bundleName, Locale.getDefault());
        this.useDefaultLocale = true;
    }

    /**
     * Initializes the bundle with a final locale.
     */
    public LocalizedBundle(String bundleName, Locale useLocale) {
        this.bundleName = bundleName;
        this.locale = useLocale;
        this.useDefaultLocale = false;
    }

    private ResourceBundle getResourceBundle() {
        // When we should use the default locale
        // and is has changed, than invalidate the bundle
        if (this.useDefaultLocale && this.locale != Locale.getDefault()) {
            this.locale = Locale.getDefault();
            this.currentResourceBundle = null;
        }

        // Initialize the resource bundle on demand
        if (this.currentResourceBundle == null) {
            currentResourceBundle = ResourceBundle.getBundle(bundleName, this.locale, new UTF8ResourceBundleControl());
        }
        return currentResourceBundle;
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
