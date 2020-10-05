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
package eu.tsystems.mms.tic.testframework.layout.extraction;

import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import org.opencv.core.Scalar;

public class BufImage implements Image {

    private java.awt.image.BufferedImage bufferedImage;
    private WritableRaster raster;

    public BufImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        raster = bufferedImage.getRaster();

    }

    @Override
    public Scalar getColorAt(int x, int y) {
        double[] doubles = null;
        doubles = raster.getPixel(x, y, doubles);
        return new Scalar(doubles);
    }

    @Override
    public void setColorAt(int x, int y, Scalar color) {
        raster.setPixel(x, y, color.val);
    }

    @Override
    public Point2D getSize() {
        return new Point2D(bufferedImage.getWidth(), bufferedImage.getHeight());
    }
}
