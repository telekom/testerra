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
