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

import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Node;
import eu.tsystems.mms.tic.testframework.layout.reporting.GraphicalReporter;
import eu.tsystems.mms.tic.testframework.report.model.Serial;
import org.opencv.core.Scalar;

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
