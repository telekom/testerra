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
