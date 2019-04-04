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
package eu.tsystems.mms.tic.testframework.imgproc.templatematching.matchers.featurebased.test;

import eu.tsystems.mms.tic.testframework.imgproc.templatematching.matchers.featurebased.Matcher;
import eu.tsystems.mms.tic.testframework.imgproc.templatematching.matchers.featurebased.Parameters;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.stream.Stream;

public class EndToEndTest extends AbstractTest {
    /**
     * tests scale invariance of template matching, multithreading, and robustness of template matcher public API parameter sets
     */
    @Test
    public void matchTemplateSlightZoom() {
        Path template = RESOURCES_DIR.resolve("pattern-angel.png");
        Path scene = RESOURCES_DIR.resolve("screenshot-pattern-angel-huge.png");

        ExpectedMatch expectedMatch = new ExpectedMatch(
                new Point(1054, 388),
                new Size(824, 178),
                50,
                20
        );

        Stream<Parameters> params = Stream.of(
                Parameters.siftSiftBf(),
                Parameters.pyramidSiftSiftFlann(),
                Parameters.pyramidFastSurfFlann(),
                Parameters.pyramidSiftBriskBf(),
                Parameters.pyramidSiftOppOrbBf(),
                Parameters.pyramidSiftSiftBf(),
                Parameters.pyramidSurfOppSurfFlann()
        );

        processConcurrently(
                params,
                param -> {
                    LOGGER.info("using parameters : " + param);
                    Matcher matcher = new Matcher(param);
                    assertCenterAndSizeMatch(matcher, template, scene, expectedMatch);
                },
                20
        );
    }
}
