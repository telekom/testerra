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
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.layout.DefaultParameter;
import eu.tsystems.mms.tic.testframework.layout.matching.LayoutMatch;
import eu.tsystems.mms.tic.testframework.layout.matching.error.ElementMovedError;
import eu.tsystems.mms.tic.testframework.layout.matching.error.GroupMovedError;
import eu.tsystems.mms.tic.testframework.layout.matching.error.LayoutFeature;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.DistanceGraph;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Node;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.NodeSpan;

import java.util.LinkedList;
import java.util.List;

/**
 * User: rnhb
 * Date: 12.06.14
 *
 * IMPORTANT: This Detector will also detect movement errors of single elements. It should be understood as MovedElementDetector
 * enhanced with the ability to detect groups in movements.
 */
public class GroupMovementDetector extends MovedElementDetector {

    private double maximalIntraGroupMovement;
    private boolean ignoreGroups;

    public GroupMovementDetector() {
        ignorePropertyKey = TesterraProperties.LAYOUTCHECK_IGNORE_MOVEMENT;
        ignoreGroups = PropertyManager.getBooleanProperty(TesterraProperties.LAYOUTCHECK_IGNORE_GROUP_MOVEMENT, false);
    }

    private void loadProperties() {
        maximalIntraGroupMovement = PropertyManager.getIntProperty(
                TesterraProperties.LAYOUTCHECK_INTRA_GROUPING_THRESHOLD,
                DefaultParameter.LAYOUTCHECK_INTRA_GROUPING_THRESHOLD);
    }

    @Override
    protected List<LayoutFeature> findFeatures(DistanceGraph distanceGraph, LayoutMatch layoutMatch) {
        loadProperties();
        List<LayoutFeature> errors = new LinkedList<LayoutFeature>();
        if (ignoreGroups) {
            LinkedList<ElementMovedError> movementErrors = getMovementErrors(distanceGraph);
            for (ElementMovedError movementError : movementErrors) {
                errors.add(movementError);
            }
        } else {
            List<List<ElementMovedError>> movementErrorGroups = getMovementErrorGroups(distanceGraph);
            for (List<ElementMovedError> bin : movementErrorGroups) {
                if (bin.size() == 1) {
                    errors.add(bin.iterator().next());
                } else {
                    NodeSpan templateSpan = new NodeSpan();
                    NodeSpan matchSpan = new NodeSpan();
                    List<ElementMovedError> innerErrors = new LinkedList<ElementMovedError>();
                    for (ElementMovedError elementMovedError : bin) {
                        templateSpan.include(elementMovedError.getTemplateNode());
                        matchSpan.include(elementMovedError.getMatchNode());
                        innerErrors.add(elementMovedError);
                        elementMovedError.clearShortPathPoints();
                    }
                    Node template = templateSpan.toNode();
                    Node match = matchSpan.toNode();
                    errors.add(new GroupMovedError(template, match, innerErrors));
                }
            }
        }
        return errors;
    }

    /**
     * Inspects the graph for movement errors and groups them in case that they moved together.
     *
     * @param distanceGraph Graph to check for movement errors.
     * @return List of Lists of movement errors that are moved together.
     */
    public List<List<ElementMovedError>> getMovementErrorGroups(DistanceGraph distanceGraph) {
        LinkedList<ElementMovedError> movementErrors = getMovementErrors(distanceGraph);
        List<List<ElementMovedError>> binnedErrorsList = new LinkedList<List<ElementMovedError>>();

        for (ElementMovedError error1 : movementErrors) {
            List<ElementMovedError> fittingBin = null;
            for (List<ElementMovedError> bin : binnedErrorsList) {
                for (ElementMovedError error2 : bin) {
                    if (isGroupedError(error1, error2)) {
                        fittingBin = bin;
                        break;
                    }
                }
                if (fittingBin != null) {
                    break;
                }
            }
            if (fittingBin == null) {
                fittingBin = new LinkedList<ElementMovedError>();
                fittingBin.add(error1);
                binnedErrorsList.add(fittingBin);
            } else {
                fittingBin.add(error1);
            }
        }
        return binnedErrorsList;
    }

    /**
     * Checks if two movement errors are a group regarding their movement.
     *
     * @param error1 First error.
     * @param error2 Second error.
     * @return True if both errors are a group.
     */
    private boolean isGroupedError(ElementMovedError error1, ElementMovedError error2) {
        return error1.getMovement().getEuclideanDistance(error2.getMovement()) <= maximalIntraGroupMovement;
    }
}
