
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
