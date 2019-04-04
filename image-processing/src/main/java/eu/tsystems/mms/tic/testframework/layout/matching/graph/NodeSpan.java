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
 * User: rnhb Date: 12.06.14
 */
public class NodeSpan {
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;

    public NodeSpan() {
        this.minX = Integer.MAX_VALUE;
        this.minY = Integer.MAX_VALUE;
        this.maxX = Integer.MIN_VALUE;
        this.maxY = Integer.MIN_VALUE;
    }

    public void include(Node node) {
        Point2D position = node.getPosition();
        Point2D size = node.getSize();
        if (position.x < minX) {
            minX = position.x;
        }
        if (position.x + size.x > maxX) {
            maxX = position.x + size.x;
        }
        if (position.y < minY) {
            minY = position.y;
        }
        if (position.y + size.y > maxY) {
            maxY = position.y + size.y;
        }
    }

    public Node toNode() {
        return new CombinedNode(minX, minY, maxX - minX, maxY - minY);
    }
}
