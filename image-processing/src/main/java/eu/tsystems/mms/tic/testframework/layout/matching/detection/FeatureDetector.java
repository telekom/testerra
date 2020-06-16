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

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;
import eu.tsystems.mms.tic.testframework.layout.matching.FeatureCategory;
import eu.tsystems.mms.tic.testframework.layout.matching.LayoutMatch;
import eu.tsystems.mms.tic.testframework.layout.matching.error.LayoutFeature;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.DistanceGraph;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Edge;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Although this class is called error detector, it can also serve as base class for detectors that detect a correct layout.
 * <p/>
 * User: rnhb
 * Date: 12.06.14
 */
public abstract class FeatureDetector {

    private static Logger logger = LoggerFactory.getLogger(FeatureDetector.class);

    /**
     * Displacement distance of matches that is considered as error.
     */
    private double displacementConsideredAsError;

    protected String ignorePropertyKey;

    private FeatureCategory featureCategory;

    public LayoutMatch detectAndAddMatches(DistanceGraph distanceGraph, LayoutMatch layoutMatch) {
        loadProperties();
        List<LayoutFeature> foundLayoutFeatures = findFeatures(distanceGraph, layoutMatch);
        List<LayoutFeature> listToAddTo = null;
        switch (featureCategory) {
            case CRITICAL:
                listToAddTo = layoutMatch.getCriticalMatches();
                break;
            case IGNORED_CRITICAL:
                listToAddTo = layoutMatch.getIgnoredCriticalMatches();
                break;
            case CORRECT:
                listToAddTo = layoutMatch.getCorrectMatches();
                break;
            default:
                logger.error(featureCategory + " is no valid Category.");
                listToAddTo = new LinkedList<LayoutFeature>();
        }
        for (LayoutFeature foundLayoutFeature : foundLayoutFeatures) {
            listToAddTo.add(foundLayoutFeature);
        }
        return layoutMatch;
    }

    private void loadProperties() {
        displacementConsideredAsError = LayoutCheck.Properties.DISPLACEMENT_THRESHOLD.asDouble();
        // This should be done more nicely in the future - every Detector who does not set the ignorePropertyKey is considered
        // as detector of correct matches!
        if (ignorePropertyKey != null) {
            boolean ignore = PropertyManager.getBooleanProperty(ignorePropertyKey, false);
            if (ignore) {
                featureCategory = FeatureCategory.IGNORED_CRITICAL;
            } else {
                featureCategory = FeatureCategory.CRITICAL;
            }
        } else {
            featureCategory = FeatureCategory.CORRECT;
        }
    }

    protected abstract List<LayoutFeature> findFeatures(DistanceGraph distanceGraph, LayoutMatch layoutMatch);

    protected boolean hasEdgeToSameLocation(List<Edge> edges, Node node) {
        boolean hasMatchAtLocation = false;
        for (Edge edge : edges) {
            if (isEdgeToSameLocation(edge, node)) {
                hasMatchAtLocation = true;
                break;
            }
        }
        return hasMatchAtLocation;
    }

    protected boolean isEdgeToSameLocation(Edge edge, Node node) {
        return edge.getOtherNode(node).getPosition().getEuclideanDistance(node.getPosition()) <= displacementConsideredAsError;
    }
}
