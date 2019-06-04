/*
 * Created on 03.13.2014
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.layout.matching.graph;

import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.core.Point2D;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * User: rnhb Date: 23.05.14
 */
public abstract class Node implements Serializable {
    /**
     * LayoutElement this Node represents in the DistanceGraph.
     */
    protected LayoutElement layoutElement;
    protected transient List<Edge> edgesToTemplateNode;
    protected transient List<Edge> edgesToMatchNodes;
    protected Point2D position;
    protected Point2D size;

    public Node() {
        edgesToTemplateNode = new LinkedList<>();
        edgesToMatchNodes = new LinkedList<>();
    }

    public Node(LayoutElement layoutElement) {
        this();
        this.layoutElement = layoutElement;
    }

    /**
     * Check the connectTo(..)-method.
     *
     * @param node Node to create an Edge to.
     * @return Created Edge.
     */
    protected abstract Edge createEdge(Node node);

    /**
     * Check the connectTo(..)-method.
     *
     * @param edge Edge to accept.
     */
    protected abstract void acceptEdge(Edge edge);

    /**
     * Connect this node to the given one.
     * <p/>
     * The problem here is that the edge has to be stored in one fo the two lists, depending on the class of the other
     * node. Therefore, the other node decides where to store it so we don't break OO principles here by using
     * instanceof.
     *
     * @param node Node to connect this one to.
     */
    public void connectTo(Node node) {
        Edge edge = node.createEdge(this);
        edge.setStartNode(this);
        acceptEdge(edge);
        edge.setLength(getTrueDistanceTo(node));
    }

    /**
     * Get the real distance to the given node. This means the size of both Nodes is taken into account.
     *
     * @param node Node to check the distance to.
     * @return Distance of the Nodes.
     */
    private double getTrueDistanceTo(Node node) {
        Point2D otherPosition = node.getPosition();
        Point2D otherSize = node.getSize();
        int dPositionX = position.x - otherPosition.x;
        int sizeXToUse = dPositionX < 0 ? size.x : otherSize.x;
        int dX = Math.max(Math.abs(dPositionX) - sizeXToUse, 0);
        int dPositionY = position.y - otherPosition.y;
        int sizeYToUse = dPositionY < 0 ? size.y : otherSize.y;
        int dY = Math.max(Math.abs(dPositionY) - sizeYToUse, 0);
        return Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
    }

    /**
     * Get the start and end point of a short Path between two nodes.
     *
     * @param node Path end node.
     * @return start and end Point of the short path, or null if they overlap
     */
    public Point2D[] getShortDistancePointsTo(Node node) {
        if (isInside(node)) {
            return null;
        }
        Point2D[] points = new Point2D[2];
        Point2D otherPosition = node.getPosition();
        Point2D otherSize = node.getSize();
        Point2D xCoordinates = getNearPoint(position.x, size.x, otherPosition.x, otherSize.x);
        Point2D yCoordinates = getNearPoint(position.y, size.y, otherPosition.y, otherSize.y);
        points[0] = new Point2D(xCoordinates.x, yCoordinates.x);
        points[1] = new Point2D(xCoordinates.y, yCoordinates.y);
        return points;
    }

    private boolean isInside(Node node) {
        boolean startInsideX = node.position.x <= position.x && node.position.x + node.size.x >= position.x;
        boolean endInsideX = node.position.x <= position.x + size.x && node.position.x + node.size.x >= position.x + size.x;
        boolean startInsideY = node.position.y <= position.y && node.position.y + node.size.y >= position.y;
        boolean endInsideY = node.position.y <= position.y + size.y && node.position.y + node.size.y >= position.y + size.y;
        return startInsideX && startInsideY || startInsideX && endInsideY || endInsideX && startInsideY || endInsideX && endInsideY;
    }

    private Point2D getNearPoint(int position, int size, int otherPosition, int otherSize) {
        Point2D coordinates = new Point2D();
        int d = position - otherPosition;
        if (d + size < 0) {
            coordinates.x = position + size;
            coordinates.y = otherPosition;
        } else if (d - otherSize > 0) {
            coordinates.x = position;
            coordinates.y = otherPosition + otherSize;
        } else {
            coordinates.x = position + size / 2;
            coordinates.y = otherPosition + otherSize / 2;
        }
        return coordinates;
    }

    public Point2D getPosition() {
        return position;
    }

    public Point2D getSize() {
        return size;
    }

    @Override
    public String toString() {
        if (edgesToMatchNodes != null && edgesToTemplateNode != null) {
            return "Node:[" + edgesToTemplateNode.size() + " templates, " + edgesToMatchNodes.size() + " matches, "
                    + "position=" + position + ", size=" + size + ']';
        }
        else {
            return "Node:[" + "position=" + position + ", size=" + size + ']';
        }
    }

    /**
     * Checks whether a LayoutElement is existing
     *
     * @param layoutElement LayoutElement to check
     * @return true if the element is existing else it returns false
     */
    public boolean hasLayoutElement(LayoutElement layoutElement) {
        return layoutElement.equals(this.layoutElement);
    }

    public List<Edge> getEdgesToTemplateNode() {
        return edgesToTemplateNode;
    }

    public List<Edge> getEdgesToMatchNodes() {
        return edgesToMatchNodes;
    }

    public LayoutElement getLayoutElement() {
        return layoutElement;
    }

    /**
     * Method checks whether there are edges existing which matches
     *
     * @param node
     * @return true or false
     */
    public boolean hasEdgeToMatchNode(MatchNode node) {
        for (Edge edge : edgesToMatchNodes) {
            if (edge.getOtherNode(this) == node) {
                return true;
            }
        }
        return false;
    }

    public void addEdgeToTemplateNode(Edge edge) {
        edgesToTemplateNode.add(edge);
    }

    public void addEdgeToMatchNode(Edge edge) {
        edgesToMatchNodes.add(edge);
    }

    public Edge getEdgeTo(Node node) {
        for (Edge edge : edgesToTemplateNode) {
            if (edge.getEndNode() == node) {
                return edge;
            }
        }
        for (Edge edge : edgesToMatchNodes) {
            if (edge.getEndNode() == node) {
                return edge;
            }
        }
        return null;
    }

    public void changePositionBy(Point2D change) {
        position = position.add(change);
        layoutElement.changePositionBy(change);
    }
}
