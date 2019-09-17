/*
 * Created on 03.13.2014
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.layout.matching.detection;

import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.layout.matching.LayoutMatch;
import eu.tsystems.mms.tic.testframework.layout.matching.error.ElementMissingError;
import eu.tsystems.mms.tic.testframework.layout.matching.error.LayoutFeature;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.DistanceGraph;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Edge;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.TemplateNode;

import java.util.LinkedList;
import java.util.List;

/**
 * User: rnhb
 * Date: 12.06.14
 */
public class ElementMissingDetector extends FeatureDetector {

    public ElementMissingDetector() {
        ignorePropertyKey = TesterraProperties.LAYOUTCHECK_IGNORE_MISSING_ELEMENTS;
    }

    @Override
    protected List<LayoutFeature> findFeatures(DistanceGraph distanceGraph, LayoutMatch layoutMatch) {
        List<LayoutFeature> errors = new LinkedList<LayoutFeature>();
        for (TemplateNode templateNode : distanceGraph.getTemplateNodes()) {
            List<Edge> edgesToMatchNodes = templateNode.getEdgesToMatchNodes();
            if (edgesToMatchNodes.size() == 0) {
                errors.add(new ElementMissingError(templateNode));
            }
        }
        return errors;
    }
}
