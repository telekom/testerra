/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.sikuli;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.sikuli.api.Screen;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 *
 */
public class WebDriverScreen implements Screen, Loggable {

    private final WebDriver driver;

    /**
     * @param driver .
     */
    public WebDriverScreen(WebDriver driver) {
        this.driver = driver;
    }

    BufferedImage crop(BufferedImage src, int x, int y, int width, int height) {
        BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = dest.getGraphics();
        g.drawImage(src, 0, 0, width, height, x, y, x + width, y + height, null);
        g.dispose();
        return dest;
    }


    @Override
    public BufferedImage getScreenshot(int x, int y, int width,
                                       int height) {
        if (driver instanceof TakesScreenshot) {
            try {
                byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                try {
                    BufferedImage full = ImageIO.read(bais);
                    BufferedImage cropped = crop(full, x, y, width, height);
                    return cropped;
                } catch (IOException e) {
                    log().warn("Error getting screenshot", e);
                } finally {
                    try {
                        bais.close();
                    } catch (IOException e) {
                        log().trace("Cannot close stream.", e);
                    }
                }

                return null;
            } finally {
                System.gc();
            }
        } else {
            throw new RuntimeException("WebDriver object is not a TakesScreenshot instance");
        }
    }

    @Override
    public Dimension getSize() {
        org.openqa.selenium.Dimension dimension = driver.manage().window().getSize();
        return new Dimension(dimension.width, dimension.height);
    }

}
