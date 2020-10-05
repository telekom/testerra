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

import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.core.Point2D;

/**
 * User: rnhb
 * Date: 23.05.14
 */
public class MatchNode extends Node {

    public MatchNode(LayoutElement layoutElement, Point2D matchedLocation) {
        super();
        this.layoutElement = new LayoutElement(matchedLocation, layoutElement.getSize());
        position = matchedLocation;
        size = layoutElement.getSize();
    }

    public void addEdgeToTemplate(Edge edge) {
        edgesToTemplateNode.add(edge);
    }

    @Override
    public Edge createEdge(Node node) {
        Edge edge = new Edge(this);
        node.addEdgeToMatchNode(edge);
        return edge;
    }

    @Override
    public void acceptEdge(Edge edge) {
        edge.getEndNode().addEdgeToMatchNode(edge);
    }
}
