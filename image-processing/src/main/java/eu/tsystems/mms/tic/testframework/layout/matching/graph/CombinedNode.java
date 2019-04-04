/*
 * Created on 03.13.2014
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.layout.matching.graph;

import eu.tsystems.mms.tic.testframework.layout.core.Point2D;

/**
 * User: rnhb
 * Date: 12.06.14
 */
public class CombinedNode extends Node {

    /**
     * A class to summarize several nodes for visualization.
     *
     * @param positionX Start x position.
     * @param positionY Start y position.
     * @param sizeX     Size in x.
     * @param sizeY     Size in y.
     */
    public CombinedNode(int positionX, int positionY, int sizeX, int sizeY) {
        position = new Point2D(positionX, positionY);
        size = new Point2D(sizeX, sizeY);
    }

    @Override
    public Edge createEdge(Node node) {
        return null;
    }

    @Override
    public void acceptEdge(Edge edge) {

    }
}
