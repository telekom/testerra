/*
 * Created on 03.13.2014
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.layout.matching.error;

import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Node;
import eu.tsystems.mms.tic.testframework.layout.reporting.GraphicalReporter;
import eu.tsystems.mms.tic.testframework.report.model.Serial;
import org.opencv.core.Scalar;

/**
 * User: rnhb
 * Date: 23.05.14
 */
public class AmbiguousElementMovedError extends LayoutFeature {

    private static final long serialVersionUID = Serial.SERIAL;

    /**
     * Error when several matches where found at wrong locations.
     * @param nodeOfElement Node representing the element with an error.
     */
    public AmbiguousElementMovedError(Node nodeOfElement) {
        super(nodeOfElement);
    }

    @Override
    public void callbackDraw(GraphicalReporter graphicalReporter) {
        LayoutElement layoutElement = nodeOfElement.getLayoutElement();
        Point2D location = layoutElement.getPosition();
        graphicalReporter.drawRect(location, location.add(layoutElement.getSize()), new Scalar(200, 0, 255), 2);
    }

    @Override
    public String toString() {
        return name + ": Element at " + nodeOfElement.getPosition() + " has several matches, but none at the correct location.";
    }
}
