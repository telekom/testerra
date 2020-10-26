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

import eu.tsystems.mms.tic.testframework.report.model.Serial;
import java.io.Serializable;
import java.util.HashSet;
import org.opencv.core.Mat;
import org.opencv.core.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: rnhb
 * Date: 16.05.14
 *
 * Class representing an area of an image, that is considered as layout element. This can also be a group of elements.
 */
public class LayoutElement implements Serializable {

    private static final long serialVersionUID = Serial.SERIAL;

    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutElement.class);
    private HashSet<Point2D> points;
    private transient Mat image;
    private Point2D[] corners;

    private final int numberOfCorners = 4;

    /**
     * Basic Constructor
     */
    public LayoutElement() {
        points = new HashSet<Point2D>();
        corners = new Point2D[numberOfCorners];
    }

    /**
     * Constructor for cases, when the element is already known.
     *
     * @param upperLeftCorner Upper left Corner of the element.
     * @param size Size of the element.
     */
    public LayoutElement(Point2D upperLeftCorner, Point2D size) {
        corners = new Point2D[numberOfCorners];
        corners[Corners.UPPER_LEFT.i] = upperLeftCorner;
        corners[Corners.UPPER_RIGHT.i] = new Point2D(upperLeftCorner.x + size.x, upperLeftCorner.y);
        corners[Corners.LOWER_LEFT.i] = new Point2D(upperLeftCorner.x, upperLeftCorner.y + size.y);
        corners[Corners.LOWER_RIGHT.i] = new Point2D(upperLeftCorner.x + size.x, upperLeftCorner.y + size.y);
    }

    public void addPoint(Point2D point) {
        points.add(point);
    }

    public Point2D getPosition() {
        return corners[Corners.UPPER_LEFT.i];
    }

    @Override
    public String toString() {
        String out = "";
        for (int i = 0; i < numberOfCorners; i++) {
            Point2D corner = corners[i];
            if (corner != null) {
                out += Corners.getName(i) + " = " + corner + "; ";
            } else {
                out += Corners.getName(i) + " = not assigned; ";
            }
        }
        if (points != null) {
            out += points.size() + " marked points.";
        }
        return out;
    }

    public Mat getImage() {
        return image;
    }

    public void setImage(Mat image) {
        this.image = image;
    }

    /**
     * Creates image information for this layoutElement.
     *
     * @param baseImage Image to extract the information from.
     */
    public void extractImageInformation(Mat baseImage) {
        int minX = Math.max(0, corners[Corners.UPPER_LEFT.i].x);
        int maxX = Math.min(baseImage.cols(), corners[Corners.UPPER_RIGHT.i].x + 1);
        int minY = Math.max(0, corners[Corners.UPPER_LEFT.i].y);
        int maxY = Math.min(baseImage.rows(), corners[Corners.LOWER_LEFT.i].y + 1);
        Range rowRange = new Range(minY, maxY);
        Range colRange = new Range(minX, maxX);
        image = new Mat(baseImage, rowRange, colRange);
        double[] firstColor = image.get(0, 0);
        for (int x = 0; x < image.width(); x++) {
            for (int y = 0; y < image.height(); y++) {
                double[] otherColor = image.get(y, x);
                for (int i = 0; i < otherColor.length; i++) {
                    if (firstColor[i] != otherColor[i]) {
                        return;
                    }
                }
            }
        }
        // if we reach this point, the image contains only a single color
        String colorString = "RGB=[";
        for (double v : firstColor) {
            colorString += v + ",";
        }
        colorString = colorString.substring(0, colorString.length() - 1);
        colorString += "]";
        throw new RuntimeException("The reference image snippet at " + corners[Corners.UPPER_LEFT.i] + " to " + corners[Corners.LOWER_RIGHT.i] +
                " does only contain the color " + colorString + ". It is highly likely that the annotation is incorrect or " +
                "uses a wrong Reference Image");
    }

    public void computeCorners() {
        Point2D anyPoint = points.iterator().next();
        corners[0] = anyPoint;
        corners[1] = anyPoint;
        corners[2] = anyPoint;
        corners[3] = anyPoint;
        for (Point2D point : points) {
            if (point.x <= corners[0].x && point.y <= corners[0].y) {
                corners[0] = point;
            }
            if (point.x >= corners[1].x && point.y <= corners[1].y) {
                corners[1] = point;
            }
            if (point.x >= corners[2].x && point.y >= corners[2].y) {
                corners[2] = point;
            }
            if (point.x <= corners[3].x && point.y >= corners[3].y) {
                corners[3] = point;
            }
        }
    }

    public Point2D getSize() {
        int sizeX = corners[Corners.UPPER_RIGHT.i].x - corners[Corners.UPPER_LEFT.i].x;
        int sizeY = corners[Corners.LOWER_RIGHT.i].y - corners[Corners.UPPER_RIGHT.i].y;
        return new Point2D(sizeX, sizeY);
    }

    public void changePositionBy(Point2D movement) {
        for (int i = 0; i < numberOfCorners; i++) {
            corners[i] = corners[i].add(movement);
        }
    }
}
