/*
 * Created on 03.13.2014
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.layout.matching;

import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.DistanceGraph;
import org.opencv.core.Mat;

import java.util.List;

/**
 * User: rnhb
 * Date: 16.05.14
 */
public abstract class TemplateMatcher {

    protected boolean referenceImageIsSubImage = false;

    /**
     * Creates a Distance Graph
     *
     * @param imageToMatch   Image to match in.
     * @param layoutElements Template Elements.
     * @return LayoutMatch.
     */
    public abstract DistanceGraph matchTemplates(Mat imageToMatch, List<LayoutElement> layoutElements);

    public void setReferenceImageIsSubImage(boolean referenceImageIsSubImage) {
        this.referenceImageIsSubImage = referenceImageIsSubImage;
    }
}
