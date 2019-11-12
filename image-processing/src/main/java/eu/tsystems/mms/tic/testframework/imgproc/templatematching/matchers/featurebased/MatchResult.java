/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
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

import eu.tsystems.mms.tic.testframework.utils.RandomUtils;
import org.javatuples.Pair;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Line2D;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static eu.tsystems.mms.tic.testframework.imgproc.templatematching.matchers.featurebased.Matcher.params;

/**
 * Created by joku on 30.09.2016.
 */
class MatchResult implements FreesNativeMemory {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchResult.class);

    final List<DMatch> knnDmatches;
    final List<DMatch> goodMatchesList;
    final List<Point> objectCorners;

    final TemplateImage template;
    final SceneImage scene;

    final Optional<Match> match;

    final Duration executionTime;

    final Output output = new Output();

    private MatchResult(TemplateImage templateImage, SceneImage sceneImage) {
        Instant startTime = Instant.now();

        DescriptorMatcher matcher = null;
        List<MatOfDMatch> knn_matches = new LinkedList<>();
        List<DMatch> knn_dmatches = new LinkedList<>();
        List<DMatch> goodMatches = new LinkedList<>();
        List<Point> objectCorners = new LinkedList<>();
        Optional<Match> match = Optional.empty();

        template = templateImage;
        scene = sceneImage;

        try {
            if (template == null || template.image == null || template.image.empty()) {
                LOGGER.error("Scene image was loaded from file but is empty. Aborting");
                return;
            }
            int matcherId = params().MATCHER;
            try {
                matcher = DescriptorMatcher.create(matcherId);
            } catch (Exception e) {
                LOGGER.error("Could not create descriptor matcher with paramater id=" + matcherId + ". Aborting. Exception was: \n" + e);
                return;
            }

            matcher.knnMatch(template.getDescriptors(), scene.getDescriptors(), knn_matches, 2);
            knn_dmatches.addAll(convert(knn_matches));

            // lowe distance filtering
            for (DMatch dMatch1 : knn_dmatches) {
                for (DMatch dMatch2 : knn_dmatches) {
                    if (dMatch1.distance < params().DESCRIPTOR_DISTANCE_LOWE * dMatch2.distance) {
                        goodMatches.add(dMatch1);
                    }
                }
            }

            if (goodMatches.size() >= 4) { // need 4 matches to calculate homography
                List<Point> templateFeatures = template.getMatchingPoints(goodMatches);
                List<Point> sceneFeatures = scene.getMatchingPoints(goodMatches);

                Pair<Mat, Mat> inliersAndHomography = guessMaskForFoundObject(templateFeatures, sceneFeatures);
                List<DMatch> inliers = filterMatchesByMask(goodMatches, inliersAndHomography.getValue0());
                output.logTemplateFeaturesAbsolute(inliers);
                output.logTemplateFeaturesMatchedPercent(inliers, template.features);
                List<Point> matchingPointsInScene = scene.getMatchingPoints(inliers);

                Mat homography = inliersAndHomography.getValue1();
                objectCorners.addAll(getFoundObjectCornersInScene(homography));

                if (!rectIntersectsSelf(objectCorners)
                        && sizeOk(objectCorners)
                        && allAnglesAlmostPerpendicular(objectCorners)
                        ) {
                    Match actualMatch = Match.from(matchingPointsInScene, inliers);
                    match = Optional.of(actualMatch);
                }

                inliersAndHomography.getValue0().release();
                inliersAndHomography.getValue1().release();
            }
        } catch (Exception e) {
            LOGGER.error("\nsomething went wrong while analyzing template: \n" + template + " and scene: \n" + scene + "\n exception was : ", e);
        } finally {
            this.match = match;
            this.knnDmatches = Collections.unmodifiableList(knn_dmatches);
            this.goodMatchesList = Collections.unmodifiableList(goodMatches);
            this.objectCorners = Collections.unmodifiableList(objectCorners);
            this.executionTime = Duration.between(startTime, Instant.now());

            if (matcher != null) matcher.clear();
            knn_matches.forEach(Mat::release);
        }
    }

    private double length(Point a, Point b) {
        return Math.sqrt(Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2));
    }

    static MatchResult calculate(TemplateImage template, SceneImage scene) {
        return new MatchResult(template, scene);
    }

    private List<DMatch> convert(List<MatOfDMatch> matchesList) {
        List<DMatch> list = new LinkedList<>();
        for (MatOfDMatch matOfDMatch : matchesList) {
            list.addAll(convert(matOfDMatch));
        }
        return list;
    }

    private List<DMatch> convert(MatOfDMatch matOfDMatch) {
        List<DMatch> list = new LinkedList<>();
        DMatch[] dMatches = matOfDMatch.toArray();
        Collections.addAll(list, dMatches);
        return list;
    }

    private List<DMatch> filterMatchesByMask(List<DMatch> goodMatchesList, Mat outputMask) {
        LinkedList<DMatch> betterMatchesList = new LinkedList<>();
        int goodMatchesListSize = goodMatchesList.size();
        for (int i = 0; i < goodMatchesListSize; i++) {
            if (outputMask.get(i, 0)[0] != 0.0) {
                betterMatchesList.add(goodMatchesList.get(i));
            }
        }
        return betterMatchesList;
    }

    /**
     * @param template_features
     * @param scene_features
     * @return inliermask and homography matrix
     */
    private Pair<Mat, Mat> guessMaskForFoundObject(List<Point> template_features, List<Point> scene_features) {
        MatOfPoint2f templateFeaturesMat = new MatOfPoint2f();
        templateFeaturesMat.fromList(template_features);

        MatOfPoint2f sceneFeaturesMat = new MatOfPoint2f();
        sceneFeaturesMat.fromList(scene_features);

        Mat inliersMask = new Mat();
        Mat homographyTransform = Calib3d.findHomography(
                templateFeaturesMat,
                sceneFeaturesMat,
                params().HOMOGRAPHY_METHOD,
                params().RANSAC_REPROJECTION_THRESHOLD,
                inliersMask
        );

        templateFeaturesMat.release();
        sceneFeaturesMat.release();

        return new Pair<>(inliersMask, homographyTransform);
    }

    private List<Point> getFoundObjectCornersInScene(Mat homographyTransform) {
        Mat templateCorners = new Mat(4, 1, CvType.CV_32FC2);
        Mat sceneCorners = new Mat(4, 1, CvType.CV_32FC2);

        templateCorners.put(0, 0, 0, 0);
        templateCorners.put(1, 0, template.image.cols(), 0);
        templateCorners.put(2, 0, template.image.cols(), template.image.rows());
        templateCorners.put(3, 0, 0, template.image.rows());

        Core.perspectiveTransform(templateCorners, sceneCorners, homographyTransform);

        List<Point> sceneCornersList = new LinkedList<>();
        sceneCornersList.add(new Point(sceneCorners.get(0, 0)));
        sceneCornersList.add(new Point(sceneCorners.get(1, 0)));
        sceneCornersList.add(new Point(sceneCorners.get(2, 0)));
        sceneCornersList.add(new Point(sceneCorners.get(3, 0)));
        templateCorners.release();
        sceneCorners.release();
        return sceneCornersList;
    }

    private boolean rectIntersectsSelf(List<Point> corners) {
        Line2D line1 = new Line2D.Double(corners.get(0).x, corners.get(0).y, corners.get(1).x, corners.get(1).y);
        Line2D line2 = new Line2D.Double(corners.get(1).x, corners.get(1).y, corners.get(2).x, corners.get(2).y);
        Line2D line3 = new Line2D.Double(corners.get(2).x, corners.get(2).y, corners.get(3).x, corners.get(3).y);
        Line2D line4 = new Line2D.Double(corners.get(3).x, corners.get(3).y, corners.get(0).x, corners.get(0).y);

        return (line1.intersectsLine(line3)) || (line2.intersectsLine(line4));
    }

    // inner angle method... buggy. use intersects lines
    private boolean innerAnglesNear360(List<Point> corners) { // inner angles must sum to ~360, else self intersection
        double innerAngleSum = Math.addExact(
                Math.addExact(
                        (long) absAngle(corners.get(0), corners.get(1), corners.get(2)),
                        (long) absAngle(corners.get(1), corners.get(2), corners.get(3))
                ),
                Math.addExact(
                        (long) absAngle(corners.get(2), corners.get(3), corners.get(0)),
                        (long) absAngle(corners.get(3), corners.get(0), corners.get(1))
                )
        );
        LOGGER.debug("angle : " + String.valueOf(innerAngleSum));
        boolean angleOk = (innerAngleSum > 0.9 * 360) && (innerAngleSum < 1.1 * 360);
        if (!angleOk) {
            LOGGER.debug("found match projection inner angles not within allowed bounds");
        }
        return angleOk;
    }

    private boolean allAnglesAlmostPerpendicular(List<Point> corners) {
        int angleThreshold = params().matchProjectionInnerAngleDegreeThreshold;
        return almostRightAngle(absAngle(corners.get(0), corners.get(1), corners.get(2)), angleThreshold)
                && almostRightAngle(absAngle(corners.get(1), corners.get(2), corners.get(3)), angleThreshold)
                && almostRightAngle(absAngle(corners.get(2), corners.get(3), corners.get(0)), angleThreshold)
                && almostRightAngle(absAngle(corners.get(3), corners.get(0), corners.get(1)), angleThreshold);
    }

    private boolean almostRightAngle(double angle, double allowedDifferenceInDegree) {
        return (angle >= 90 - allowedDifferenceInDegree) && (angle <= 90 + allowedDifferenceInDegree);
    }

    double absAngle(Point left, Point angleRoot, Point right) {
        double angle = Math.abs(
                Math.toDegrees(
                        Math.atan2(left.y - angleRoot.y, left.x - angleRoot.x)
                                - Math.atan2(right.y - angleRoot.y, right.x - angleRoot.x)));
        if (angle > 180) { // only return the sharp angle! never the other side
            angle = angle - 180;
        }
        return angle;
    }

    private boolean sizeOk(List<Point> objectCorners) {
        Point p0 = objectCorners.get(0);
        Point p1 = objectCorners.get(1);
        Point p2 = objectCorners.get(2);
        Point p3 = objectCorners.get(3);

        double matchCircumference = length(p0, p1)
                + length(p1, p2)
                + length(p2, p3)
                + length(p3, p0);
        double templateCircumference = template.image.size().height * 2 + template.image.size().width * 2;

        LOGGER.debug("match circumference not ok");
        return (matchCircumference < templateCircumference * 4) && (matchCircumference > templateCircumference * 0.25);
    }

    private double size(Mat mat) {
        return mat.size().height;
    }

    @Override
    public void release() {
        template.release();
        scene.release();
    }

    class Output {
        void logTemplateFeaturesAbsolute(List<DMatch> inlier_matches) {
            int size = countMatchedTemplateFeatures(inlier_matches);
            LOGGER.debug(size + " template features are matched to scene after filtering.");
        }

        void logTemplateFeaturesMatchedPercent(List<DMatch> inlier_matches, MatOfKeyPoint templateAllFeatures) {
            int matched_template_features_count = countMatchedTemplateFeatures(inlier_matches);
            BigDecimal matchingPercent = new BigDecimal(100 / (size(templateAllFeatures) / matched_template_features_count));
            matchingPercent = matchingPercent.setScale(2, BigDecimal.ROUND_UP);
            LOGGER.debug(matchingPercent + "% of template features have a matching descriptor in the search picture.");
        }

        void writeDebugPicture(Path fullPath) {
            if (!match.isPresent()) {
                return;
            }
            Mat image = new Mat();
            MatOfDMatch matches = new MatOfDMatch();
            Match m = match.get();
            matches.fromList(m.matches);
            Mat sceneImage = scene.getImage();
            drawObjectCorners(sceneImage);
            Features2d.drawMatches(
                    template.getImage(),
                    template.getFeatures(),
                    sceneImage,
                    scene.getFeatures(),
                    matches,
                    image);
            int templateWidth = template.getImage().width();

            Scalar color = new Scalar(
                    RandomUtils.generateRandomInt(255),
                    RandomUtils.generateRandomInt(255),
                    RandomUtils.generateRandomInt(255)
            );

            drawCenter(image, new Point(m.getCenter().x + templateWidth, m.getCenter().y), color);
            Core.rectangle(
                    image,
                    new Point(
                            (double) (m.getRect().x + templateWidth),
                            (double) (m.getRect().y)
                    ),
                    new Point(
                            (double) (m.getRect().x + m.getRect().width + templateWidth),
                            (double) (m.getRect().y + m.getRect().height)
                    ),
                    color
            );

            fullPath.toFile().getParentFile().mkdirs();
            Highgui.imwrite(fullPath.toAbsolutePath().toString(), image);
            LOGGER.info("verbose matching information image written : " + fullPath);

            image.release();
            matches.release();
            sceneImage.release();
        }

        private int countMatchedTemplateFeatures(List<DMatch> inliers) {
            int count = 0;
            Set<Integer> templateFeatureIds = new HashSet<>();
            for (DMatch inlier : inliers) {
                Integer queryIdx = inlier.queryIdx;
                if (!templateFeatureIds.contains(queryIdx)) {
                    templateFeatureIds.add(queryIdx);
                    count++;
                }
            }
            return count;
        }

        void drawObjectCorners(Mat image) {
            Core.line(image, objectCorners.get(0), objectCorners.get(1), new Scalar(0, 255, 0), 4);
            Core.line(image, objectCorners.get(1), objectCorners.get(2), new Scalar(0, 255, 0), 4);
            Core.line(image, objectCorners.get(2), objectCorners.get(3), new Scalar(0, 255, 0), 4);
            Core.line(image, objectCorners.get(3), objectCorners.get(0), new Scalar(0, 255, 0), 4);
        }

        private void drawCenter(Mat image, Point center, Scalar color) {
            Core.circle(image, center, 15, color, 3);
        }
    }
}
