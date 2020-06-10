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
package eu.tsystems.mms.tic.testframework.layout.matching.detection;

import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.layout.matching.LayoutMatch;
import eu.tsystems.mms.tic.testframework.layout.matching.error.AmbiguousMatchError;
import eu.tsystems.mms.tic.testframework.layout.matching.error.LayoutFeature;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.DistanceGraph;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Edge;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.MatchNode;

import java.util.LinkedList;
import java.util.List;

/**
 * User: rnhb
 * Date: 12.06.14
 * <p/>
 * Error detector for cases when a match is matched on several templates.
 */
public class AmbiguousMatchDetector extends FeatureDetector {

    public AmbiguousMatchDetector() {
        ignorePropertyKey = TesterraProperties.LAYOUTCHECK_IGNORE_AMBIGUOUS_MATCH;
    }

    @Override
    protected List<LayoutFeature> findFeatures(DistanceGraph distanceGraph, LayoutMatch layoutMatch) {
        List<LayoutFeature> errors = new LinkedList<LayoutFeature>();
        for (MatchNode matchNode : distanceGraph.getMatchNodes()) {
            List<Edge> edgesToTemplateNode = matchNode.getEdgesToTemplateNode();
            if (edgesToTemplateNode.size() > 1) {
                if (!hasEdgeToSameLocation(edgesToTemplateNode, matchNode)) {
                    errors.add(new AmbiguousMatchError(matchNode));
                }
            }
        }
        return errors;
    }
}
