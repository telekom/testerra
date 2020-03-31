
package eu.tsystems.mms.tic.testframework.layout.matching.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * User: rnhb Date: 23.05.14
 */
public class Edge implements Serializable {
    /**
     * Logger
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Node startNode;
    private Node endNode;
    private double length;

    /**
     * Edge between two Nodes in a DistanceGraph
     *
     * @param endNode Node to end this edge in.
     */
    public Edge(Node endNode) {
        this.endNode = endNode;
    }

    /**
     * Sets the start node of this edge.
     *
     * @param startNode Start Node.
     */
    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    /**
     * Sets the length of this Node.
     * @param distance Length.
     */
    public void setLength(double distance) {
        this.length = distance;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "startNode=" + startNode.getPosition() +
                ", endNode=" + endNode.getPosition() +
                ", length=" + length +
                '}';
    }

    public Node getEndNode() {
        return endNode;
    }

    public Node getStartNode() {
        return startNode;
    }

    public double getLength() {
        return length;
    }

    public void swapNodes(MatchNode nodeToBeSwapped, MatchNode nodeToSwapWith) {
        if (startNode.equals(nodeToBeSwapped)) {
            startNode = nodeToSwapWith;
        }
        if (endNode.equals(nodeToBeSwapped)) {
            endNode = nodeToSwapWith;
        }
        nodeToSwapWith.addEdgeToTemplate(this);
    }

    public Node getOtherNode(Node node) {
        if (startNode.equals(node)) {
            return endNode;
        }
        if (!endNode.equals(node)) {
            logger.trace("Nodedetection Problem occurred");
        }
        return startNode;
    }
}
