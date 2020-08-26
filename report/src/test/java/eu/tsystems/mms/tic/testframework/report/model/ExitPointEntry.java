/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.report.model;


import eu.tsystems.mms.tic.testframework.report.pageobjects.abstracts.AbstractResultTableFailureEntry;
import java.util.ArrayList;
import java.util.List;

public class ExitPointEntry extends AbstractResultTableFailureEntry {

    private static final String TITLE_LABEL_NOT_EXPLICIT = "No explicit exit points due to collected assertions";
    private static final String TITLE_PATTERN = "%s (%d Tests)";

    private boolean explicitness;


    // Explicit means, that the Exit Point appears as a single entry. Otherwise it is sum up in a general Exit Point
    // TODO: Could be removed, since all Exit Points are explicit
    public boolean isExplicit() {
        return explicitness;
    }

    public void setExplicitness(boolean isExplicit) {
        this.explicitness = isExplicit;
    }

    public ExitPointEntry(TestResultHelper.TestResultFailurePointEntryType failurePointEntryType, int entryNumber, int numberOfTests, String description, boolean isExplicit, List<String> methodDetailsPaths, List<String> methodDetailsAssertions) {
        super(ResultTableFailureType.EXIT_POINT, failurePointEntryType, entryNumber, numberOfTests, description, methodDetailsPaths, methodDetailsAssertions);
        this.explicitness = isExplicit;
        if (!isExplicit()) {
            changeTitle();
        }
    }

    public ExitPointEntry(TestResultHelper.TestResultFailurePointEntryType failurePointEntryType, int entryNumber, int numberOfTests, String description, boolean isExplicit) {
        this(failurePointEntryType, entryNumber, numberOfTests, description, isExplicit, new ArrayList<>(), new ArrayList<>());
    }

    protected void changeTitle() {
        setTitle(String.format(TITLE_PATTERN, TITLE_LABEL_NOT_EXPLICIT, getNumberOfTests()));
    }
}
