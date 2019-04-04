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
package eu.tsystems.mms.tic.testframework.imgproc.templatematching.matchers.featurebased;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.KeyPoint;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

abstract class AbstractTemplateOrSceneImage extends Image {
    AbstractTemplateOrSceneImage(Path path) throws Exception {
        super(path);
    }

    AbstractTemplateOrSceneImage(Mat image) throws Exception {
        super(image);
    }

    List<Point> getMatchingPoints(List<DMatch> matches) {
        List<Point> pts = new LinkedList<>();
        List<KeyPoint> allSceneFeatures = getFeaturesList();
        for (DMatch dMatch : matches) {
            pts.add(
                    allSceneFeatures.get(
                            getIdx(dMatch)
                    ).pt
            );
        }
        return pts;
    }

    abstract int getIdx(DMatch dmatch);
}
