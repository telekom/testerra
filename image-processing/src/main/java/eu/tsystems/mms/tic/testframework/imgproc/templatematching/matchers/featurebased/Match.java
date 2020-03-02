/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.imgproc.templatematching.matchers.featurebased;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.features2d.DMatch;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by joku on 04.10.2016.
 */
public class Match {
    final Point centerRect;
    final Point centerAllPoints;
    final Rect rect;

    public Point getCenter() {
        return centerRect;
    }

    public Rect getRect() {
        return rect;
    }

    final List<DMatch> matches;
    final List<Point> points;

    private Match(List<Point> pts, List<DMatch> matches) {
        points = new LinkedList<>();
        points.addAll(pts);
        centerAllPoints = calculateCenter(pts);
        rect = calculateRect(pts);
        centerRect = calculateCenter(rect);
        this.matches = matches;
    }

    static Match from(List<Point> pts, List<DMatch> matches) {
        return new Match(pts, matches);
    }

    private Rect calculateRect(List<Point> pts) {
        //get biggest and smallest x and y

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;
        for (Point pt : pts) {
            if (pt.x < minX) minX = (int) pt.x;
            if (pt.x > maxX) maxX = (int) pt.x;
            if (pt.y < minY) minY = (int) pt.y;
            if (pt.y > maxY) maxY = (int) pt.y;
        }
        return new Rect(minX, minY, maxX - minX, maxY - minY);
    }

    private Point calculateCenter(List<Point> pts) {
        int sumX = 0;
        int sumY = 0;
        for (Point pt : pts) {
            sumX += pt.x;
            sumY += pt.y;
        }
        return new Point(
                sumX / pts.size(),
                sumY / pts.size());
    }

    private Point calculateCenter(Rect rect) {
        return new Point(
                rect.x + rect.width / 2,
                rect.y + rect.height / 2);
    }
}
