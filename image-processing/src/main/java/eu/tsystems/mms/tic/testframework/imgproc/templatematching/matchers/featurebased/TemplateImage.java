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
import org.opencv.features2d.DMatch;

import java.nio.file.Path;

class TemplateImage extends AbstractTemplateOrSceneImage {
    TemplateImage(Path path) throws Exception {
        super(path);
    }

    TemplateImage(Mat image) throws Exception {
        super(image);
    }

    @Override
    int getIdx(DMatch dmatch) {
        return dmatch.queryIdx;
    }
}
