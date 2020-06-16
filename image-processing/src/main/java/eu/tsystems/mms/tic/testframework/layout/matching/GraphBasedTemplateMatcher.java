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

import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;
import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.core.ValuedPoint2D;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.DistanceGraph;
import eu.tsystems.mms.tic.testframework.layout.matching.matchers.TemplateMatchingAlgorithm;
import org.opencv.core.Mat;

import java.util.LinkedList;
import java.util.List;

/**
 * User: rnhb
 * Date: 11.06.14
 */
public class GraphBasedTemplateMatcher extends TemplateMatcher {

    private double minimalDistanceBetweenMatches = LayoutCheck.Properties.MIN_MATCH_DISTANCE.asDouble();

    private final TemplateMatchingAlgorithm templateMatchingAlgorithm;

    public GraphBasedTemplateMatcher(TemplateMatchingAlgorithm templateMatchingAlgorithm) {
        this.templateMatchingAlgorithm = templateMatchingAlgorithm;
    }

    /**
     * After detecting all template elements, find all matches for them and construct a distance graph from that.
     *
     * @param imageToMatch   Image to match in.
     * @param layoutElements Template Elements.
     * @return DistanceGraph
     */
    @Override
    public DistanceGraph matchTemplates(Mat imageToMatch, List<LayoutElement> layoutElements) {
        DistanceGraph distanceGraph = generateInitialDistanceGraph(layoutElements);
        for (LayoutElement layoutElement : layoutElements) {
            List<ValuedPoint2D> matchingPoints = templateMatchingAlgorithm.findMatchingPoints(layoutElement, imageToMatch);
            List<ValuedPoint2D> isolatedPoints = cleanGroupedPoints(matchingPoints);
            for (ValuedPoint2D isolatedPoint : isolatedPoints) {
                distanceGraph.createMatchNode(layoutElement, isolatedPoint);
            }
        }
        distanceGraph.combineMatchNodes();
        distanceGraph.connectMatchNodes();
        distanceGraph.checkForParameterWarnings();
        if (referenceImageIsSubImage) {
            distanceGraph.incorporateSubImageDisplacement();
        }
        distanceGraph.printFullReport();
        return distanceGraph;
    }

    /**
     * Sometimes matches are found on the same object at different pixels, for example two matches 1 pixel apart from each other,
     * because both scored very high. We need to detect such cases and regard it as a single match.
     *
     * @param matchedPoints Points where a match is found.
     * @return List of locations of actually matched object.
     */
    private List<ValuedPoint2D> cleanGroupedPoints(List<ValuedPoint2D> matchedPoints) {
        if (matchedPoints.size() == 1) {
            return matchedPoints;
        }

        List<ValuedPoint2D> isolatedPoints = new LinkedList<>();
        while (!matchedPoints.isEmpty()) {
            float x = 0;
            float y = 0;
            float value = 0;
            float count = 0;
            List<ValuedPoint2D> pointsInArea = getPointsInArea(matchedPoints, minimalDistanceBetweenMatches);
            for (ValuedPoint2D point : pointsInArea) {
                matchedPoints.remove(point);
                x += point.x;
                y += point.y;
                value += point.value;
                count++;
            }
            isolatedPoints.add(new ValuedPoint2D(Math.round(x / count), Math.round(y / count), value / count));
        }
        return isolatedPoints;
    }

    /**
     * Returns all Points in an area defined by 'MINIMAL_DISTANCE_BETWEEN_MATCHES', so we can summarize them into one point.
     *
     * @param allPoints List of all points where this method should look for areas of near points.
     * @return The first area that is found.
     */
    private List<ValuedPoint2D> getPointsInArea(List<ValuedPoint2D> allPoints, double maximalDistance) {
        ValuedPoint2D basePoint;
        if (allPoints.isEmpty())
            return new LinkedList<>();
        else basePoint = allPoints.get(0);

        List<ValuedPoint2D> pointsInArea = new LinkedList<>();
        pointsInArea.add(basePoint);
        for (ValuedPoint2D point : allPoints) {
            if (point != basePoint) {
                boolean pointIsNear = false;
                for (ValuedPoint2D potentialNearPoint : pointsInArea) {
                    if (point.getEuclideanDistance(potentialNearPoint) < maximalDistance) {
                        pointIsNear = true;
                        break;
                    }
                }
                if (pointIsNear) {
                    pointsInArea.add(point);
                }
            }
        }
        return pointsInArea;
    }


    /**
     * Generates a Distance Graph with all template nodes in it.
     *
     * @param layoutElements Templates
     * @return DistanceGraph
     */
    private DistanceGraph generateInitialDistanceGraph(List<LayoutElement> layoutElements) {
        DistanceGraph distanceGraph = new DistanceGraph();
        for (LayoutElement layoutElement : layoutElements) {
            distanceGraph.createTemplateNode(layoutElement);
        }
        return distanceGraph;
    }
}
