/*
 * Testerra
 *
 * (C) 2020, René Habermann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.layout.matching;

import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.matching.error.LayoutFeature;
import java.util.ArrayList;
import java.util.List;

/**
 * User: rnhb
 * Date: 21.05.14
 */
public class LayoutMatch {
    private List<ElementMatch> matches = new ArrayList<ElementMatch>();
    private List<LayoutFeature> criticalMatches = new ArrayList<LayoutFeature>();
    private List<LayoutFeature> ignoredCriticalMatches = new ArrayList<LayoutFeature>();
    private List<LayoutFeature> correctMatches = new ArrayList<LayoutFeature>();

    /**
     * Object to store matches we found.
     */
    public LayoutMatch() {
    }

    public void addMatch(LayoutElement templateElement, LayoutElement matchedElement, double rating) {
        ElementMatch match = new ElementMatch(templateElement, matchedElement, rating);
        addMatch(match);
    }

    public List<ElementMatch> getMatches() {
        return matches;
    }

    public void addMatch(ElementMatch match) {
        matches.add(match);
    }

    public List<LayoutFeature> getCriticalMatches() {
        return criticalMatches;
    }

    public void addCriticalMatch(LayoutFeature layoutFeature) {
        criticalMatches.add(layoutFeature);
    }

    public List<LayoutFeature> getIgnoredCriticalMatches() {
        return ignoredCriticalMatches;
    }

    public List<LayoutFeature> getCorrectMatches() {
        return correctMatches;
    }
}
