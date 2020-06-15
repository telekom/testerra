
package eu.tsystems.mms.tic.testframework.layout.matching.graph;

import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;

/**
 * User: rnhb
 * Date: 23.05.14
 */
public class TemplateNode extends Node {
    public TemplateNode(LayoutElement layoutElement) {
        super(layoutElement);
        position = layoutElement.getPosition();
        size = layoutElement.getSize();
    }

    @Override
    public Edge createEdge(Node node) {
        Edge edge = new Edge(this);
        node.addEdgeToTemplateNode(edge);
        return edge;
    }

    @Override
    public void acceptEdge(Edge edge) {
        edge.getEndNode().addEdgeToTemplateNode(edge);
    }
}
