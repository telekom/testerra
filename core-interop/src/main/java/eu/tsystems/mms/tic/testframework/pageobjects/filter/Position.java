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
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.pageobjects.filter;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

/**
 * Created by rnhb on 28.07.2015.
 */
public class Position extends WebElementFilter {

    private LocationChecker locationChecker;

    Position() {
    }

    public Position(LocationChecker locationChecker) {
        this.locationChecker = locationChecker;
    }

    public Position is(int x, int y) {
        return new Position(new LocationChecker.Is(x, y));
    }

    public Position isBetween(int minX, int minY, int maX, int maxY) {
        return new Position(new LocationChecker.IsBetween(minX, minY, maX, maxY));
    }

    @Override
    public boolean isSatisfiedBy(WebElement webElement) {
        checkCorrectUsage(STD_ERROR_MSG, locationChecker);
        Point location = webElement.getLocation();
        return locationChecker.check(location.x, location.y);
    }

    @Override
    public String toString() {
        return String.format(locationChecker.toString(), "Position");
    }
}
