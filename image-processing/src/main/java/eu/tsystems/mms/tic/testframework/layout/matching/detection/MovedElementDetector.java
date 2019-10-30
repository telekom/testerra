/*
 * Created on 03.13.2014
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.layout.matching.detection;

import eu.tsystems.mms.tic.testframework.layout.LayoutCheck;
import eu.tsystems.mms.tic.testframework.layout.matching.LayoutMatch;
import eu.tsystems.mms.tic.testframework.layout.matching.error.ElementMovedError;
import eu.tsystems.mms.tic.testframework.layout.matching.error.LayoutFeature;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.DistanceGraph;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Edge;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Node;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.TemplateNode;

import java.util.LinkedList;
import java.util.List;

/**
 * User: rnhb
 * Date: 12.06.14
 *
 * IMPORTANT: Do not use this Detector together with a GroupMovementDetector, as both detect movement errors (also of a single
 * element).
 */
public class MovedElementDetector extends FeatureDetector {

    public MovedElementDetector() {
        ignorePropertyKey = LayoutCheck.Properties.LAYOUTCHECK_IGNORE_MOVEMENT.toString();
    }

    @Override
    protected List<LayoutFeature> findFeatures(DistanceGraph distanceGraph, LayoutMatch layoutMatch) {
        List<LayoutFeature> errors = new LinkedList<LayoutFeature>();
        LinkedList<ElementMovedError> movementErrors = getMovementErrors(distanceGraph);
        for (ElementMovedError movementError : movementErrors) {
            errors.add(movementError);
        }
        return errors;
    }

    protected LinkedList<ElementMovedError> getMovementErrors(DistanceGraph distanceGraph) {
        LinkedList<ElementMovedError> errorList = new LinkedList<ElementMovedError>();
        for (TemplateNode templateNode : distanceGraph.getTemplateNodes()) {
            List<Edge> edgesToMatchNodes = templateNode.getEdgesToMatchNodes();
            if (edgesToMatchNodes.size() == 1) {
                // get first
                Edge edge = edgesToMatchNodes.get(0);
                if (!isEdgeToSameLocation(edge, templateNode)) {
                    Node otherNode = edge.getOtherNode(templateNode);
                    errorList.add(new ElementMovedError(templateNode, otherNode));
                }
            }
        }
        return errorList;
    }
}
