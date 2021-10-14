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

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class SimpleLocalization {
    public static final String BUNDLE_NAME = "lang";
    private static LocalizedBundle bundle;
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleLocalization.class);

    public static String getText(final String label) {
        return getDefaultBundle().getString(label);
    }

    public static LocalizedBundle getDefaultBundle() {
        if (bundle == null) {
            setDefault(Locale.getDefault());
        }
        return bundle;
    }

    public static LocalizedBundle setDefault(Locale locale) {
        LOGGER.info("Change default locale to: " + locale);
        bundle = new LocalizedBundle(BUNDLE_NAME, locale);
        return bundle;
    }
}
