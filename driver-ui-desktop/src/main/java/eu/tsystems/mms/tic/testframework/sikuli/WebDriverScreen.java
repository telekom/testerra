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
package eu.tsystems.mms.tic.testframework.sikuli;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.sikuli.api.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 *
 */
public class WebDriverScreen implements Screen {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TakesScreenshot driver;
    private final Dimension size;

    /**
     * @param driver .
     * @throws IOException .
     */
    public WebDriverScreen(TakesScreenshot driver) throws IOException {
        this.driver = driver;
        byte[] screenshotAs = driver.getScreenshotAs(OutputType.BYTES);
        if (screenshotAs != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(screenshotAs);
            BufferedImage b = ImageIO.read(bis);
            bis.close();
            //		File screenshotFile = driver.getScreenshotAs(OutputType.FILE);
            //		BufferedImage b = ImageIO.read(screenshotFile);
            size = new Dimension(b.getWidth(), b.getHeight());
        } else {
            size = new Dimension();
        }
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
        byte[] bytes = driver.getScreenshotAs(OutputType.BYTES);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            BufferedImage full = ImageIO.read(bais);
            BufferedImage cropped = crop(full, x, y, width, height);
            return cropped;
        } catch (IOException e) {
            logger.warn("Error getting screenshot", e);
        } finally {
            try {
                bais.close();
            } catch (IOException e) {
                logger.trace("Cannot close stream.", e);
            }
        }
        return null;
    }

    @Override
    public Dimension getSize() {
        return size;
    }

}
