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

import eu.tsystems.mms.tic.testframework.layout.matching.LayoutMatch;
import eu.tsystems.mms.tic.testframework.layout.matching.error.CorrectMatch;
import eu.tsystems.mms.tic.testframework.layout.matching.error.LayoutFeature;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.DistanceGraph;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Edge;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Node;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.TemplateNode;

import java.util.LinkedList;
import java.util.List;

public class CorrectMatchDetector extends FeatureDetector {

    @Override
    protected List<LayoutFeature> findFeatures(DistanceGraph distanceGraph, LayoutMatch layoutMatch) {
        List<LayoutFeature> errors = new LinkedList<LayoutFeature>();
        List<CorrectMatch> movementErrors = getMovementErrors(distanceGraph);
        for (CorrectMatch movementError : movementErrors) {
            errors.add(movementError);
        }
        return errors;
    }

    protected List<CorrectMatch> getMovementErrors(DistanceGraph distanceGraph) {
        List<CorrectMatch> correctMatches = new LinkedList<CorrectMatch>();
        for (TemplateNode templateNode : distanceGraph.getTemplateNodes()) {
            List<Edge> edgesToMatchNodes = templateNode.getEdgesToMatchNodes();
            for (Edge edgeToMatchNode : edgesToMatchNodes) {
                if (isEdgeToSameLocation(edgeToMatchNode, templateNode)) {
                    Node otherNode = edgeToMatchNode.getOtherNode(templateNode);
                    correctMatches.add(new CorrectMatch(templateNode, otherNode));
                    break;
                }
            }
        }
        return correctMatches;
    }
}
