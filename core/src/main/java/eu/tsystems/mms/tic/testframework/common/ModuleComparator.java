/*
 * Testerra
 *
 * (C) 2022, Martin Großmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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


package eu.tsystems.mms.tic.testframework.common;

import java.util.Comparator;
import java.util.List;

/**
 * Created on 05.07.2022
 *
 * @author mgn
 */
public class ModuleComparator implements Comparator<String> {

    /**
     * These are the currently known Testerra core modules which should always initialized
     * before all other modules.
     *
     * A custom module 'AMyModule' is always sorted after the core modules
     */
    private final List<String> coreModules = List.of("CoreHook", "DriverUiHook", "DriverUi_Desktop");

    @Override
    public int compare(String o1, String o2) {
        String o1Compare = this.getStringForCompare(o1);
        String o2Compare = this.getStringForCompare(o2);
        return o1Compare.compareTo(o2Compare);
    }

    private String getStringForCompare(final String string) {
        return this.coreModules.contains(string) ? "core_" + string : "ext_" + string;
    }
}
