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
package eu.tsystems.mms.tic.testframework.layout.matching;

import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.DistanceGraph;
import org.opencv.core.Mat;

import java.util.List;

/**
 * User: rnhb
 * Date: 16.05.14
 */
public abstract class TemplateMatcher {

    protected boolean referenceImageIsSubImage = false;

    /**
     * Creates a Distance Graph
     *
     * @param imageToMatch   Image to match in.
     * @param layoutElements Template Elements.
     * @return LayoutMatch.
     */
    public abstract DistanceGraph matchTemplates(Mat imageToMatch, List<LayoutElement> layoutElements);

    public void setReferenceImageIsSubImage(boolean referenceImageIsSubImage) {
        this.referenceImageIsSubImage = referenceImageIsSubImage;
    }
}
