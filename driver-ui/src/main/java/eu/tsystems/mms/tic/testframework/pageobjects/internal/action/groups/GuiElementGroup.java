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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.action.groups;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import java.util.*;

/**
 * Created by rnhb on 24.02.2016.
 */
public class GuiElementGroup {

    private final List<GuiElement> guiElements;
    private final Map<GuiElement, Point> guiElementLocations;
    private final Map<GuiElement, Dimension> guiElementSizes;

    private boolean positionAndSizeUpdateNecessary = false;
    private Dimension groupSize;
    private Point groupPosition;
    private String name;

    public GuiElementGroup(String name) {
        this(name, new ArrayList<>());
    }

    private GuiElementGroup(String name, List<GuiElement> guiElements) {
        this.guiElements = guiElements;
        guiElementLocations = new HashMap<>();
        guiElementSizes = new HashMap<>();
        this.name = name;
    }

    public void add(GuiElement guiElement) {
        guiElements.add(guiElement);
        positionAndSizeUpdateNecessary = true;
    }

    public Dimension getSize() {
        if (positionAndSizeUpdateNecessary) {
            updatePositionAndSize();
        }
        return groupSize;
    }

    public Point getPosition() {
        if (positionAndSizeUpdateNecessary) {
            updatePositionAndSize();
        }
        return groupPosition;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    private void updatePositionAndSize() {
        groupPosition = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

        Point maximalPosition = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

        for (GuiElement guiElement : guiElements) {
            Dimension size = guiElement.getSize();
            Point location = guiElement.getLocation();

            guiElementLocations.put(guiElement, location);
            guiElementSizes.put(guiElement, size);

            groupPosition.x = Math.min(groupPosition.x, location.x);
            groupPosition.y = Math.min(groupPosition.y, location.y);

            maximalPosition.x = Math.max(maximalPosition.x, location.x + size.width);
            maximalPosition.y = Math.max(maximalPosition.y, location.y + size.height);
        }

        groupSize = new Dimension(maximalPosition.x - groupPosition.x, maximalPosition.y - groupPosition.y);

        positionAndSizeUpdateNecessary = false;
    }

    public List<GuiElement> elements() {
        List<GuiElement> guiElementList = Collections.unmodifiableList(guiElements);
        return guiElementList;
    }

    /**
     * Call this only if the elements inside the group changed their size or moved. Moving is always relative to the
     * screen coordinates - so for example, scrolling is movement.
     * <p>
     * So, call this after scrolling.
     */
    public void groupSizeOrLocationChanged() {
        positionAndSizeUpdateNecessary = true;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " " + name;
    }
}
