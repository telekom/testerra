package eu.tsystems.mms.tic.testframework.layout.matching.error;

import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Node;
import eu.tsystems.mms.tic.testframework.layout.reporting.GraphicalReporter;
import eu.tsystems.mms.tic.testframework.report.model.Serial;
import org.opencv.core.Scalar;

/**
 * Created by rnhb on 09.12.2014.
 */
public class CorrectMatch extends LayoutFeature {

    private static final long serialVersionUID = Serial.SERIAL;

    private Node matchNode;

    public CorrectMatch(Node nodeOfElement, Node matchNode) {
        super(nodeOfElement);
        this.matchNode = matchNode;
    }

    @Override
    public void callbackDraw(GraphicalReporter graphicalReporter) {
        LayoutElement layoutElement = nodeOfElement.getLayoutElement();
        Point2D templateElementLocation = layoutElement.getPosition();
        graphicalReporter.drawRect(templateElementLocation, templateElementLocation.add(layoutElement.getSize()),
                new Scalar(40, 200, 40), 2);
    }

    @Override
    public String toString() {
        return name + ": Element at "
                + nodeOfElement.getPosition()
                + " correctly matched to " + matchNode.getPosition();
    }
}
