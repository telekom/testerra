/*
 * Created on 03.13.2014
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.layout.reporting;

import eu.tsystems.mms.tic.testframework.layout.ImageUtil;
import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import eu.tsystems.mms.tic.testframework.layout.matching.LayoutMatch;
import eu.tsystems.mms.tic.testframework.layout.matching.error.LayoutFeature;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * User: rnhb
 * Date: 21.05.14
 */
public class GraphicalReporter {

    private Mat image;

    public void report(LayoutMatch layoutMatch, Mat actualImage, String outputName) {
        image = ImageUtil.copyImage(actualImage);

        // Ignored Critical matches are drawn anyway, as requested
        for (LayoutFeature ignoredCriticalMatch : layoutMatch.getIgnoredCriticalMatches()) {
            ignoredCriticalMatch.callbackDraw(this);
        }

        for (LayoutFeature layoutFeature : layoutMatch.getCorrectMatches()) {
            layoutFeature.callbackDraw(this);
        }

        for (LayoutFeature criticalMatch : layoutMatch.getCriticalMatches()) {
            criticalMatch.callbackDraw(this);
        }

        ImageUtil.writeImage(image, outputName);
        image.release();
    }

    public void drawRect(Point2D start, Point2D end, Scalar color, int thickness) {
        Core.rectangle(image, start.toOpenCvPoint(), end.toOpenCvPoint(), new Scalar(255, 255, 255), thickness + 2, 8, 0);
        Core.rectangle(image, start.toOpenCvPoint(), end.toOpenCvPoint(), color, thickness, 8, 0);
    }

    public void drawLine(Point2D start, Point2D end, Scalar color, int thickness) {
        Core.line(image, start.toOpenCvPoint(), end.toOpenCvPoint(), color, thickness, Core.LINE_AA, 0);
    }

    public void drawArrow(Point2D start, Point2D end, Scalar color, int thickness) {
        drawArrow(start, end, color, thickness, 3.14d / 16d, 15);
    }

    public void drawArrow(Point2D start, Point2D end, Scalar color, int thickness, double arrowDegree, double arrowLength) {
        drawLine(start, end, color, thickness);
        double PI = 3.14d;

        Point2D diffVector = new Point2D(end.x - start.x, end.y - start.y);
        double z = diffVector.getEuclideanLength();

        // better not ask :P
        double alpha;
        if (diffVector.x >= 0) {
            alpha = Math.acos(diffVector.y / z);
        } else if (diffVector.y >= 0) {
            alpha = Math.asin(diffVector.x / z);
        } else {
            alpha = Math.asin(-diffVector.x / z) - PI;
        }

        double alpha1 = alpha + arrowDegree;
        double alpha2 = alpha - arrowDegree;

        int y1 = end.y - (int) Math.round(Math.cos(alpha1) * arrowLength);
        int y2 = end.y - (int) Math.round(Math.cos(alpha2) * arrowLength);
        int x1 = end.x - (int) Math.round(Math.sin(alpha1) * arrowLength);
        int x2 = end.x - (int) Math.round(Math.sin(alpha2) * arrowLength);

        drawLine(new Point2D(x1, y1), end, color, thickness);
        drawLine(new Point2D(x2, y2), end, color, thickness);
    }
}
