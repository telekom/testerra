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
package eu.tsystems.mms.tic.testframework.layout.core;

import eu.tsystems.mms.tic.testframework.layout.extraction.Direction;
import org.opencv.core.Point;

import java.io.Serializable;

/**
 * User: rnhb
 * Date: 16.05.14
 */
public class Point2D implements Serializable {
    public int x;
    public int y;

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point2D() {
        this.x = 0;
        this.y = 0;
    }

    public Point2D(Point2D point) {
        this.x = point.x;
        this.y = point.y;
    }

    public Point2D(double x, double y) {
        this.x = (int) (x);
        this.y = (int) (y);
    }

    public Point2D getNeighbor(Direction direction) {
        return new Point2D(x + direction.dX, y + direction.dY);
    }

    /**
     * from OpenCV Point
     *
     * @return
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * from OpenCV Point
     *
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Point2D)) {
            return false;
        }
        Point2D it = (Point2D) obj;
        return x == it.x && y == it.y;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    public void copyPosition(Point2D point) {
        this.x = point.x;
        this.y = point.y;
    }

    public double getEuclideanLength() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public double getEuclideanDistance(Point2D point) {
        return Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2));
    }

    public Point2D subtractPoint(Point2D point) {
        return new Point2D(x - point.x, y - point.y);
    }

    public Point toOpenCvPoint() {
        return new Point(x, y);
    }

    public void moveX(int diffX) {
        x += diffX;
    }

    public void moveY(int diffY) {
        y += diffY;
    }

    public Point2D add(Point2D point) {
        return new Point2D(x + point.x, y + point.y);
    }

    public void multiplyWith(double factor) {
        x *= factor;
        y *= factor;
    }
}
