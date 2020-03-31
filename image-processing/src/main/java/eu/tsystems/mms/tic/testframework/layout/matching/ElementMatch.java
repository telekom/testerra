
package eu.tsystems.mms.tic.testframework.layout.matching;

import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.core.Point2D;

/**
 * User: rnhb
 * Date: 21.05.14
 */
public class ElementMatch {

    private final Point2D locationDifference;
    private final LayoutElement matchedElement;
    private final double rating;
    private final LayoutElement templateElement;

    public ElementMatch(LayoutElement templateElement, LayoutElement matchedElement) {
        this(templateElement, matchedElement, 1);
    }

    public ElementMatch(LayoutElement templateElement, LayoutElement matchedElement, double rating) {
        this.templateElement = templateElement;
        this.matchedElement = matchedElement;
        this.rating = rating;
        Point2D templateLocation = templateElement.getPosition();
        Point2D matchedLocation = matchedElement.getPosition();
        locationDifference = new Point2D(Math.abs(templateLocation.x - matchedLocation.x),
                Math.abs(templateLocation.y - matchedLocation.y));
    }

    public Point2D getLocationDifference() {
        return locationDifference;
    }

    public double getRating() {
        return rating;
    }

    public LayoutElement getMatchedElement() {
        return matchedElement;
    }

    public LayoutElement getTemplateElement() {
        return templateElement;
    }
}
