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

import eu.tsystems.mms.tic.testframework.layout.core.Point2D;

import java.util.LinkedList;

/**
 * User: rnhb Date: 11.06.14
 */
public class MatchNodeBin {

    private static double allowedDistanceForMatches = 2.5d;

    private Point2D position;
    private Point2D size;
    private LinkedList<MatchNode> nodes;

    public MatchNodeBin(Point2D position, Point2D size) {
        this.size = size;
        this.position = position;
        nodes = new LinkedList<MatchNode>();
    }

    public boolean isInBin(MatchNode node) {
        if (!node.getSize().equals(size)) {
            return false;
        }
        Point2D otherPosition = node.getPosition();
        return position.getEuclideanDistance(otherPosition) <= allowedDistanceForMatches;
    }

    public void addNode(MatchNode node) {
        nodes.add(node);
    }

    public int size() {
        return nodes.size();
    }

    /**
     * Combines all nodes into one, changing the edges.
     * 
     * @return All MatchNodes that objects that were combined and can be deleted.
     */
    public LinkedList<MatchNode> combineNodes() {
        MatchNode first = nodes.getFirst();
        nodes.removeFirst();
        for (MatchNode node : nodes) {
            for (Edge edge : node.getEdgesToTemplateNode()) {
                edge.swapNodes(node, first);
            }
        }
        return nodes;
    }
}
