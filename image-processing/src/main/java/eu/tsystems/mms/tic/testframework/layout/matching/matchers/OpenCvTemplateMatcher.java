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
package eu.tsystems.mms.tic.testframework.layout.matching.matchers;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.layout.DefaultParameter;
import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.core.ValuedPoint2D;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.LinkedList;

public class OpenCvTemplateMatcher implements
    TemplateMatchingAlgorithm,
    Loggable
{

    public enum MatchingMode {
        // SQDIFF(0),
        SQDIFF_NORMED(1),
        // CCORR(2),
        CCORR_NORMED(3),
        // CCOEFF(4),
        CCOEFF_NORMED(5);
        public int i;

        MatchingMode(int i) {
            this.i = i;
        }
    }

    /**
     * Defines at which score a region is considered a match. Should be as high as possible and as low as needed.
     */
    protected double matchThreshold = PropertyManager.getDoubleProperty(
            TesterraProperties.LAYOUTCHECK_MATCH_THRESHOLD,
            DefaultParameter.LAYOUTCHECK_MATCH_THRESHOLD);

    protected MatchingMode matchingMode;

    public OpenCvTemplateMatcher(MatchingMode matchingMode) {
        this.matchingMode = matchingMode;
    }

    /**
     * Finds all points in the picture that match the given layoutElement.
     * The matchThreshold defines which score is needed for a match.
     * For each template there can be several matches, sometimes even only 1 pixel from each other.
     *
     * @param layoutElement Template
     * @param imageToMatch  Image to search for matches
     * @return List of matches.
     */
    @Override
    public LinkedList<ValuedPoint2D> findMatchingPoints(LayoutElement layoutElement, Mat imageToMatch) {
        Mat template = layoutElement.getImage();
        int resultCols = imageToMatch.cols() - template.cols() + 1;
        int resultRows = imageToMatch.rows() - template.rows() + 1;
        Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);
        Imgproc.matchTemplate(imageToMatch, template, result, matchingMode.i);
        LinkedList<ValuedPoint2D> matchedPoints = new LinkedList<>();
        for (int x = 0; x < resultCols; x++) {
            for (int y = 0; y < resultRows; y++) {
                double value = result.get(y, x)[0];
                if (value > matchThreshold) {
                    ValuedPoint2D p = new ValuedPoint2D(x, y, value);
                    log().info(String.format("Found point %s for image %s with match: %f > %f", p, template.size(), value, matchThreshold));
                    matchedPoints.add(p);
                }
            }
        }
        return matchedPoints;
    }
}
