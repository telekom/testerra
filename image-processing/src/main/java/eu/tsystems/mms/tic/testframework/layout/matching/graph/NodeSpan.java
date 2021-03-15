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

/**
 * User: rnhb Date: 12.06.14
 */
public class NodeSpan {
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;

    public NodeSpan() {
        this.minX = Integer.MAX_VALUE;
        this.minY = Integer.MAX_VALUE;
        this.maxX = Integer.MIN_VALUE;
        this.maxY = Integer.MIN_VALUE;
    }

    public void include(Node node) {
        Point2D position = node.getPosition();
        Point2D size = node.getSize();
        if (position.x < minX) {
            minX = position.x;
        }
        if (position.x + size.x > maxX) {
            maxX = position.x + size.x;
        }
        if (position.y < minY) {
            minY = position.y;
        }
        if (position.y + size.y > maxY) {
            maxY = position.y + size.y;
        }
    }

    public Node toNode() {
        return new CombinedNode(minX, minY, maxX - minX, maxY - minY);
    }
}
