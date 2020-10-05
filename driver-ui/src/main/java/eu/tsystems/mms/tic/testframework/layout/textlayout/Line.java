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
package eu.tsystems.mms.tic.testframework.layout.textlayout;

import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class Line {
    private Point2D begin;
    private Point2D end;

    public Line(int x, int y) {
        this.begin = new Point2D(x, y);
    }

    public void setEndPoint(int x, int y) {
        end = new Point2D(x, y);
    }

    public double length() {
        return end.getEuclideanDistance(begin);
    }

    public int distanceTo(int x, int y) {
        return Math.abs(x - begin.x) + Math.abs(y - begin.y);
    }

    @Override
    public String toString() {
        return begin.toString() + (end == null ? "" : " to " + end.toString());
    }

    public void markIn(Mat mat) {
        Core.line(mat, begin.toOpenCvPoint(), end.toOpenCvPoint(), new Scalar(255, 100, 100), 1, Core.LINE_4, 0);
    }
}
