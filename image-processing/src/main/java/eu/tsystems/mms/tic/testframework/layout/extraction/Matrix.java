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
 */
package eu.tsystems.mms.tic.testframework.layout.extraction;

import eu.tsystems.mms.tic.testframework.layout.core.Point2D;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class Matrix implements Image {

    private org.opencv.core.Mat mat;

    public Matrix(Mat mat) {
        this.mat = mat;
    }

    @Override
    public Scalar getColorAt(int x, int y) {
        double[] doubles = mat.get(y, x);
        Scalar color = new Scalar(doubles);
        return color;
    }

    @Override
    public void setColorAt(int x, int y, Scalar color) {
        mat.put(x, y, color.val);
    }

    @Override
    public Point2D getSize() {
        return new Point2D(mat.width(), mat.height());
    }
}
