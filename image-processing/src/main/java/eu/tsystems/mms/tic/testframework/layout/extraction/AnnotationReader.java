/*
 * Created on 03.13.2014
 *
 * Copyright(c) 2011 - 2014 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.layout.extraction;

import eu.tsystems.mms.tic.testframework.annotator.AnnotationContainer;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import eu.tsystems.mms.tic.testframework.layout.DefaultParameter;
import eu.tsystems.mms.tic.testframework.layout.LayoutComparator;
import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * User: rnhb Date: 16.05.14
 * <p/>
 * Class for reading image information from an annotated image and a reference image.
 */
public class AnnotationReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationReader.class);

    /**
     * If more than this fraction of pixels is marked in the marking color, we assume a wrongly set marking color.
     */
    private double maximalFractionOfMarkedPixels;

    /**
     * If less than these pixels are marked, we assume that no annotation was made.
     */
    private int minimumMarkedPixels;

    /**
     * Extract all points of the marking color
     *
     * @param annotatedImage
     * @return all points of the marking color
     */
    public HashSet<Point2D> getMarkedPixels(XetaImage annotatedImage) {
        Scalar markingColorBGR = annotatedImage.getColorAt(0, 0);
        HashSet<Point2D> markedPoints = new HashSet<Point2D>();
        Point2D size = annotatedImage.getSize();
        for (int x = 0; x < size.x; x++) {
            for (int y = 0; y < size.y; y++) {
                Scalar pointColor = annotatedImage.getColorAt(x, y);
                if (pointColor.equals(markingColorBGR)) {
                    markedPoints.add(new Point2D(x, y));
                }
            }
        }
        markedPoints.remove(new Point2D(0, 0));
        loadProperties();

        if (markedPoints.size() > size.x * size.y * maximalFractionOfMarkedPixels) {
            throw new FennecSystemException(
                    LayoutComparator.Messages.tooManyMarkedPixels("" + maximalFractionOfMarkedPixels * 100));
        }

        if (markedPoints.size() <= minimumMarkedPixels) {
            throw new FennecSystemException(LayoutComparator.Messages.tooFewMarkedPixels());
        }
        return markedPoints;
    }

    /**
     * Extracts the elements that are marked with the marking color.
     *
     * @param annotatedImage Image to extract annotation information from
     * @return List of LayoutElement
     */
    public List<Rectangle> readAnnotationDimensions(BufferedImage annotatedImage) {
        XetaBufferedImage xetaBufferedImage = new XetaBufferedImage(annotatedImage);

        HashSet<Point2D> markedPoints = null;
        try {
            markedPoints = getMarkedPixels(xetaBufferedImage);
        } catch (FennecSystemException e) {
            LOGGER.warn(e.getMessage());
        }

        if (markedPoints != null) {
            List<LayoutElement> layoutElements = extractLayoutElementDimensions(markedPoints);
            List<Rectangle> annotatedAreas = new ArrayList<Rectangle>();
            for (LayoutElement layoutElement : layoutElements) {
                Point2D position = layoutElement.getPosition();
                Point2D size = layoutElement.getSize();
                Rectangle rectangle = new Rectangle(position.x + 1, position.y + 1, size.x - 2, size.y - 2);
                annotatedAreas.add(rectangle);
            }
            return annotatedAreas;
        }
        return null;
    }

    /**
     * Extracts the annotations from the persisted container.
     *
     * @param baseImage Image to extract image information from
     * @param annotationContainer Container with list of annotations.
     * @return List of LayoutElement
     */
    public List<LayoutElement> extractAnnotatedElementsFromAnnotationContainer(Mat baseImage,
            AnnotationContainer annotationContainer) {
        List<LayoutElement> layoutElements = new ArrayList<LayoutElement>();
        for (Rectangle rectangle : annotationContainer.getAnnotations()) {
            Point location = rectangle.getLocation();
            Dimension size = rectangle.getSize();
            LayoutElement element = new LayoutElement(new Point2D(location.getX(), location.getY()),
                    new Point2D(size.getWidth(), size.getHeight()));
            element.extractImageInformation(baseImage);
            layoutElements.add(element);
        }

        return layoutElements;
    }

    /**
     * Extracts the elements that are marked with the marking color.
     *
     * @param baseImage Image to extract image information from
     * @param annotatedImage Image to extract annotation information from
     * @return List of LayoutElement
     */
    public List<LayoutElement> extractAnnotatedLayoutElements(Mat baseImage, Mat annotatedImage) {
        XetaMat xetaMat = new XetaMat(annotatedImage);
        HashSet<Point2D> markedPoints = getMarkedPixels(xetaMat);

        List<LayoutElement> layoutElements = extractLayoutElementDimensions(markedPoints);

        // Add image information to the layoutElements
        for (LayoutElement layoutElement : layoutElements) {
            layoutElement.extractImageInformation(baseImage);
        }

        return layoutElements;
    }

    private List<LayoutElement> extractLayoutElementDimensions(HashSet<Point2D> markedPoints) {
        List<LayoutElement> layoutElements = new LinkedList<LayoutElement>();

        // Use the points to create layoutElements
        while (markedPoints.size() > 0) {
            Point2D startingPoint = markedPoints.iterator().next();
            LayoutElement layoutElement = createLayoutElement(startingPoint, markedPoints);
            if (layoutElement != null) {
                if (layoutElement.getSize().x <= 3 && layoutElement.getSize().y <= 3) {
                    LOGGER.error(
                            "Found marked rectangle " + layoutElement + ", but it is tiny so it will be ignored," +
                                    " because an error in the Annotation is assumed.");
                } else {
                    layoutElements.add(layoutElement);
                    LOGGER.debug("Found marked rectangle " + layoutElement);
                }
            }
        }

        if (layoutElements.size() == 0) {
            LOGGER.warn(
                    "No Templates could be extracted from the annotated reference image, although the marking color " +
                            "seems to be set right. Check your Annotations!");
        }
        return layoutElements;
    }

    private void loadProperties() {
        maximalFractionOfMarkedPixels = PropertyManager.getDoubleProperty(
                FennecProperties.LAYOUTCHECK_MAXIMUM_MARKED_PIXELS_RATIO,
                DefaultParameter.LAYOUTCHECK_MAXIMUM_MARKED_PIXELS_RATIO);
        minimumMarkedPixels = PropertyManager.getIntProperty(
                FennecProperties.LAYOUTCHECK_MINIMUM_MARKED_PIXELS,
                DefaultParameter.LAYOUTCHECK_MINIMUM_MARKED_PIXELS);
    }

    private LayoutElement createLayoutElement(Point2D startingPoint, HashSet<Point2D> markedPoints) {
        LayoutElement layoutElement;

        // Check if there is a neighboring point that is marked
        Direction currentDirection = findDirectionOfMarkedNeighbor(startingPoint, markedPoints);

        if (currentDirection == Direction.NONE) {
            markedPoints.remove(startingPoint);
            LOGGER.debug(
                    "Point " + startingPoint + " is in marking color, but not surrounded by any other marked pixels.");
            return null;
        } else {
            layoutElement = new LayoutElement();
            layoutElement.addPoint(startingPoint);
        }

        // After having a starting point, we test if there is a closed layoutElement
        Point2D currentPoint = new Point2D(startingPoint);
        do {
            if (hasNeighboringMarkedPointInDirection(currentPoint, currentDirection, markedPoints)) {
                currentPoint = expandFromPointInDirection(currentPoint, currentDirection, layoutElement, markedPoints);
            } else if (hasNeighboringMarkedPointInDirection(currentPoint, currentDirection.next(), markedPoints)) {
                currentDirection = currentDirection.next();
                currentPoint = expandFromPointInDirection(currentPoint, currentDirection, layoutElement, markedPoints);
            } else if (hasNeighboringMarkedPointInDirection(currentPoint, currentDirection.previous(), markedPoints)) {
                currentDirection = currentDirection.previous();
                currentPoint = expandFromPointInDirection(currentPoint, currentDirection, layoutElement, markedPoints);
            } else {
                LOGGER.error("The layoutElement [" + layoutElement
                        + "] was not closed, deleting. If you get this error a lot," +
                        " your annotation color is probably ill chosen, as it appears in the Screenshot already.");
                return null;
            }
        } while (!currentPoint.equals(startingPoint));
        layoutElement.computeCorners();
        return layoutElement;
    }

    private Direction findDirectionOfMarkedNeighbor(Point2D point, HashSet<Point2D> markedPoints) {
        Direction direction = Direction.RIGHT;
        for (int i = 0; i <= 3; i++) {
            if (markedPoints.contains(point.getNeighbor(direction))) {
                return direction;
            } else {
                direction = direction.next();
            }
        }
        return Direction.NONE;
    }

    private boolean hasNeighboringMarkedPointInDirection(Point2D point, Direction direction,
            HashSet<Point2D> markedPoints) {
        Point2D testedPoint = point.getNeighbor(direction);
        return markedPoints.contains(testedPoint);
    }

    private Point2D expandFromPointInDirection(Point2D pointToExpandFrom,
            Direction expansionDirection,
            LayoutElement rectangle,
            HashSet<Point2D> markedPoints) {
        Point2D newPoint = pointToExpandFrom.getNeighbor(expansionDirection);
        rectangle.addPoint(newPoint);
        markedPoints.remove(newPoint);
        return newPoint;
    }
}
