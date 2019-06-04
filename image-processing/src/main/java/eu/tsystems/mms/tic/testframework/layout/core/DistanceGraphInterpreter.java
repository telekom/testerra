/*
 * Created on 03.13.2014
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.layout.core;

import eu.tsystems.mms.tic.testframework.layout.matching.LayoutMatch;
import eu.tsystems.mms.tic.testframework.layout.matching.detection.FeatureDetector;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.DistanceGraph;

import java.util.LinkedList;

/**
 * User: rnhb
 * Date: 11.06.14
 *
 * Class that collects error detectors to run them on a distance graph.
 */
public class DistanceGraphInterpreter {

    private LinkedList<FeatureDetector> featureDetectors;

    /**
     * Basic Constructor.
     */
    public DistanceGraphInterpreter() {
        featureDetectors = new LinkedList<FeatureDetector>();
    }

    /**
     * Adds an error Detector.
     *
     * @param featureDetector Error Detector to add.
     */
    public void addErrorDetector(FeatureDetector featureDetector) {
        featureDetectors.add(featureDetector);
    }

    /**
     * Process a distance Graph to extract errors.
     *
     * @param distanceGraph DistanceGraph to process.
     * @return LayoutMatch.
     */
    public LayoutMatch generateLayoutErrors(DistanceGraph distanceGraph) {
        LayoutMatch layoutMatch = new LayoutMatch();
        for (FeatureDetector featureDetector : featureDetectors) {
            featureDetector.detectAndAddMatches(distanceGraph, layoutMatch);
        }
        return layoutMatch;
    }
}
