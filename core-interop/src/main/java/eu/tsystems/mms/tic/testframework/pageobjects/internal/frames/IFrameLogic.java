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

package eu.tsystems.mms.tic.testframework.pageobjects.internal.frames;

import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import java.util.List;

@Deprecated
public interface IFrameLogic {
    /**
     * Returns a list of frames as GuiElement in switching order.
     *
     * @return List of frames as GuiElement in switching order
     */
    List<UiElement> getAllFramesInOrder();

    /**
     * Switches to the frame the GuiElement is inside in.
     */
    void switchToCorrectFrame();

    /**
     * Switch to the default content.
     */
    void switchToDefaultFrame();

    UiElement[] getFrames();

    boolean hasFrames();
}
