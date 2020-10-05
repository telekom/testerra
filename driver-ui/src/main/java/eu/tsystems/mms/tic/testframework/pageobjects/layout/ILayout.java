/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.pageobjects.layout;

import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;

@Deprecated
public interface ILayout {
    LayoutBorders getElementLayoutBorders(UiElement GuiElementFacade);

    ILayout leftOf(UiElement distanceGE);

    void checkOn(UiElement actualGE, Assertion configuredAssert);

    String toStringText();

    ILayout above(UiElement distanceGE);

    ILayout rightOf(UiElement distanceGE);

    ILayout below(UiElement distanceGE);

    ILayout sameTop(UiElement distanceGE, int delta);

    ILayout sameBottom(UiElement distanceGE, int delta);

    ILayout sameLeft(UiElement distanceGE, int delta);

    ILayout sameRight(UiElement distanceGE, int delta);

    @Deprecated
    public static class LayoutBorders {
        long left = -1;
        long right = -1;
        long top = -1;
        long bottom = -1;

        LayoutBorders(long left, long right, long top, long bottom) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }

        public long getLeft() {
            return left;
        }

        public long getRight() {
            return right;
        }

        public long getTop() {
            return top;
        }

        public long getBottom() {
            return bottom;
        }
    }
}
