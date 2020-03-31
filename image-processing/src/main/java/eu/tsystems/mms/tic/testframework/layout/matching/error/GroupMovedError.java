
package eu.tsystems.mms.tic.testframework.layout.matching.error;

import eu.tsystems.mms.tic.testframework.layout.matching.graph.Node;
import eu.tsystems.mms.tic.testframework.layout.reporting.GraphicalReporter;

import java.util.List;

/**
 * User: rnhb
 * Date: 12.06.14
 */
public class GroupMovedError extends ElementMovedError {

//    private static final long serialVersionUID = 5916487820398860284L;

    private List<ElementMovedError> innerErrors;

    public GroupMovedError(Node groupedNode, Node matchNode, List<ElementMovedError> innerErrors) {
        super(groupedNode, matchNode);
        this.innerErrors = innerErrors;
    }

    @Override
    public String toString() {
        return name + ": Group of " + innerErrors.size() + " elements at " + super.toString();
    }

    @Override
    public void callbackDraw(GraphicalReporter graphicalReporter) {
        for (ElementMovedError innerError : innerErrors) {
            innerError.drawUnderstated(graphicalReporter);
        }
        super.callbackDraw(graphicalReporter);
    }

    public List<ElementMovedError> getSingleElementMovedErrors() {
        return innerErrors;
    }
}
