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
 * Date: 21.05.14
 */
public class ElementMissingError extends LayoutFeature {

    private static final long serialVersionUID = Serial.SERIAL;

    public ElementMissingError(Node nodeOfElement) {
        super(nodeOfElement);
    }

    @Override
    public void callbackDraw(GraphicalReporter graphicalReporter) {
        LayoutElement layoutElement = nodeOfElement.getLayoutElement();
        Point2D templateElementLocation = layoutElement.getPosition();
        graphicalReporter.drawRect(templateElementLocation, templateElementLocation.add(layoutElement.getSize()),
                new Scalar(0, 0, 255), 2);
    }

    @Override
    public String toString() {
        return name + ": Element at "
                + nodeOfElement.getPosition()
                + " was not found.";
    }
}
