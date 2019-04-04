/*
 * Created on 03.13.2014
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.layout.matching.detection;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
import eu.tsystems.mms.tic.testframework.layout.DefaultParameter;
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
        displacementConsideredAsError = PropertyManager.getIntProperty(
                FennecProperties.LAYOUTCHECK_DISPLACEMENT_THRESHOLD,
                DefaultParameter.LAYOUTCHECK_DISPLACEMENT_THRESHOLD);
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
