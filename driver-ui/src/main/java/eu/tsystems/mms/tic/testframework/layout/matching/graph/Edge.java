/*
 * Testerra
 *
 * (C) 2020, René Habermann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.layout.matching.graph;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
