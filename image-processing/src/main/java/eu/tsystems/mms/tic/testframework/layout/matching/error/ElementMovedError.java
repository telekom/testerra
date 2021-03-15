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

import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.Node;
import eu.tsystems.mms.tic.testframework.layout.reporting.GraphicalReporter;
import eu.tsystems.mms.tic.testframework.report.model.Serial;
import org.opencv.core.Scalar;

/**
 * User: rnhb
 * Date: 21.05.14
 */
public class ElementMovedError extends LayoutFeature {

    private static final long serialVersionUID = Serial.SERIAL;

    protected Node matchNode;
    private Point2D[] shortPathPoints;

    private transient Scalar templateColor = new Scalar(52, 145, 248);
    private transient Scalar matchColor = new Scalar(52, 52, 248);
    private transient Scalar lineColor = new Scalar(52, 100, 248);
    private int templateRectWidth = 2;
    private int matchRectWidth = 2;
    private int lineWidth = 1;


    public ElementMovedError(Node nodeOfElement, Node matchNode) {
        super(nodeOfElement);
        this.matchNode = matchNode;
        this.shortPathPoints = nodeOfElement.getShortDistancePointsTo(matchNode);
    }

    public Point2D getMovement() {
        return matchNode.getPosition().subtractPoint(nodeOfElement.getPosition());
    }

    @Override
    public void callbackDraw(GraphicalReporter graphicalReporter) {
        draw(graphicalReporter, templateRectWidth, matchRectWidth, lineWidth);
    }

    private void draw(GraphicalReporter graphicalReporter, int templateRectWidth, int matchRectWidth, int lineWidth) {
        Point2D matchLocation = matchNode.getPosition();
        Point2D templateLocation = nodeOfElement.getPosition();
        graphicalReporter.drawRect(templateLocation, templateLocation.add(nodeOfElement.getSize()), templateColor, templateRectWidth);
        graphicalReporter.drawRect(matchLocation, matchLocation.add(matchNode.getSize()), matchColor, matchRectWidth);
        if (shortPathPoints != null) {
            graphicalReporter.drawArrow(shortPathPoints[0], shortPathPoints[1], lineColor, lineWidth);
        }
    }

    protected void drawUnderstated(GraphicalReporter graphicalReporter) {
        draw(graphicalReporter, 1, 1, 1);
    }

    @Override
    public String toString() {
        return name + ": " + nodeOfElement.getPosition()
                + " moved to "
                + matchNode.getPosition()
                + ", Difference is "
                + matchNode.getPosition().subtractPoint(nodeOfElement.getPosition()) + ".";
    }

    public Node getMatchNode() {
        return matchNode;
    }

    public Node getTemplateNode() {
        return nodeOfElement;
    }

    /**
     * This will set the shortPathPoints to null, so that no linking line between the errors is drawn.
     */
    public void clearShortPathPoints() {
        shortPathPoints = null;
    }
}
