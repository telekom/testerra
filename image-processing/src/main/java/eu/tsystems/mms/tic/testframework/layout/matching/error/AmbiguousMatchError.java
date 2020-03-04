
package eu.tsystems.mms.tic.testframework.layout.matching.error;

import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Node;
import eu.tsystems.mms.tic.testframework.layout.reporting.GraphicalReporter;
import eu.tsystems.mms.tic.testframework.report.model.Serial;
import org.opencv.core.Scalar;

/**
 * User: rnhb
 * Date: 12.06.14
 */
public class AmbiguousMatchError extends LayoutFeature {

    private static final long serialVersionUID = Serial.SERIAL;

    /**
     * Error when a match matches several templates, but can't be assigned to a concrete one.
     *
     * @param nodeOfElement Node representing the element with an error.
     */
    public AmbiguousMatchError(Node nodeOfElement) {
        super(nodeOfElement);
    }

    @Override
    public void callbackDraw(GraphicalReporter graphicalReporter) {
        LayoutElement layoutElement = nodeOfElement.getLayoutElement();
        Point2D location = layoutElement.getPosition();
        graphicalReporter.drawRect(location, location.add(layoutElement.getSize()), new Scalar(200, 0, 255), 2);
    }

    @Override
    public String toString() {
        return name + ": Match at " + nodeOfElement.getPosition() + " has several matching templates, but none at its own location.";
    }
}
