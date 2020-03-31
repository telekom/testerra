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
package eu.tsystems.mms.tic.testframework.sikuli;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URL;

/**
 * <Beschreibung der Klasse>
 *
 * @author pele
 */
public interface SikuliDriver extends WebDriver {

    /**
     * finds element by image URL
     *
     * @param imageUrl .
     * @return .
     */
    ImageElement findImageElement(URL imageUrl);

    /**
     * finds element by location
     *
     * @param x .
     * @param y .
     * @return .
     */
    WebElement findElementByLocation(int x, int y);
}
