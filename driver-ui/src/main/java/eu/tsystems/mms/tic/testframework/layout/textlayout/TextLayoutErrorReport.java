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
package eu.tsystems.mms.tic.testframework.layout.textlayout;

import eu.tsystems.mms.tic.testframework.layout.ImageUtil;
import java.util.List;
import org.opencv.core.Mat;

public class TextLayoutErrorReport {

    private int correctLinePixelCount;
    private int correctTextPixelCount;
    private int errorPixelCount;
    private float errorRatio;
    private Mat reportMat;
    private String message;
    private String imageDestination;

    public static TextLayoutErrorReport createReport(List<Line> lines, Mat textMat, String imageDestination) {
        TextLayoutErrorReport textLayoutErrorReport = new TextLayoutErrorReport();

        textLayoutErrorReport.imageDestination = imageDestination;

        Mat reportMat = ImageUtil.createMat(textMat.rows(), textMat.cols());
        Mat lineMat = ImageUtil.createMat(textMat.rows(), textMat.cols());
        for (Line line : lines) {
            line.markIn(lineMat);
        }
        for (int col = 0; col < reportMat.cols(); col++) {
            for (int row = 0; row < reportMat.rows(); row++) {
                double[] textValues = textMat.get(row, col);
                double[] lineValues = lineMat.get(row, col);
                boolean hasLine = 0 < lineValues[0] + lineValues[1] + lineValues[2];
                boolean hasText = 0 < textValues[0] + textValues[1] + textValues[2];
                double red = Math.max(20, reportMat.get(row, col)[2]);

                if (hasLine) {
                    textLayoutErrorReport.correctLinePixelCount++;
                }

                if (hasText) {
                    textLayoutErrorReport.correctTextPixelCount++;
                }

                if (hasLine & hasText) {
                    textLayoutErrorReport.errorPixelCount++;
                    red = 180;
                    markArea(reportMat, col, row, 8, 180);
                }
                reportMat.put(row, col,
                        hasLine & !hasText ? 180 : 20,
                        hasText & !hasLine ? 180 : 20,
                        red);
            }
        }
        textLayoutErrorReport.reportMat = reportMat;
        textLayoutErrorReport.errorRatio = 100f * textLayoutErrorReport.errorPixelCount / (textLayoutErrorReport.errorPixelCount + textLayoutErrorReport.correctLinePixelCount + textLayoutErrorReport.correctTextPixelCount);

        return textLayoutErrorReport;
    }

    private static void markArea(Mat mat, int col, int row, int size, int red) {
        for (int i = -size; i <= size; i++) {
            for (int j = -size; j <= size; j++) {
                int x = col + i;
                int y = row + j;
                if (x >= 0 && y >= 0 && x < mat.cols() && y < mat.rows()) {
                    double[] v = mat.get(y, x);
                    v[2] = red;
                    mat.put(y, x, v);
                }
            }
        }
    }

    public Mat getReportMat() {
        return reportMat;
    }

    public int getCorrectLinePixelCount() {
        return correctLinePixelCount;
    }

    public int getCorrectTextPixelCount() {
        return correctTextPixelCount;
    }

    public int getErrorPixelCount() {
        return errorPixelCount;
    }

    public float getErrorRatio() {
        return errorRatio;
    }

    public boolean isErrorThresholdExceeded(double errorThreshold) {
        String numberAddendum = errorThreshold < 1 ? "%" : " pixel";
        boolean errorThresholdExceeded;
        if (errorThreshold < 1) {
            errorThresholdExceeded = errorRatio > errorThreshold;
        } else {
            errorThresholdExceeded = errorPixelCount > errorThreshold;
        }
        message = "Error threshold of " + errorThreshold + numberAddendum
                + " was exceeded for text layout error detection. Actual error is " +
                (errorThreshold < 1 ? errorRatio : errorPixelCount) + numberAddendum + ".";

        return errorThresholdExceeded;
    }

    public String getMessage() {
        return message;
    }

    public String getImageDestination() {
        return imageDestination;
    }
}
