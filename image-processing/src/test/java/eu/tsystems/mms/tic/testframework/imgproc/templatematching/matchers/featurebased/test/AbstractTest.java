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

import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.imgproc.templatematching.matchers.featurebased.Match;
import eu.tsystems.mms.tic.testframework.imgproc.templatematching.matchers.featurebased.Matcher;
import eu.tsystems.mms.tic.testframework.layout.OpenCvInitializer;
import org.opencv.core.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by joku on 19.09.2016.
 */
public class AbstractTest {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractTest.class);

    protected static final Path RESOURCES_DIR = Paths.get("src/test/resources/templatematching/matchers/featurebased");

    static {
        OpenCvInitializer.init();
    }

    protected <T> void processConcurrently(Stream<T> input, Consumer<T> consumer, int parallelism) {
        ExecutorService pool = Executors.newWorkStealingPool(parallelism);
        input.map(
                element -> pool.submit(
                        () -> {
                            consumer.accept(element);
                        }
                )
        ).collect(Collectors.toList()).forEach(
                future -> {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        Assert.fail("error while executing feature based matcher", e.getCause());
                    }
                }
        );
    }

    protected void assertCenterAndSizeMatch(Matcher matcher, Path template, Path scene, ExpectedMatch expectedMatch) {
        Optional<Match> result = matcher.match(template, scene);
        Assert.assertTrue(result.isPresent(), "result found!");
        Match match = result.get();
        Point actualCenter = match.getCenter();
        int distance = distance(match.getCenter(), expectedMatch.center);
        AssertCollector.assertEquals(
                distance <= expectedMatch.maxDistanceOfActualAndExpectedCenterInPx,
                true,
                "Actual optionalMatch " + actualCenter + " is too far from expected position " + expectedMatch.center + ". Distance " + distance + ", Threshold for passing is " + expectedMatch.maxDistanceOfActualAndExpectedCenterInPx + ".");
        double actualArea = match.getRect().size().area();
        double expectedArea = expectedMatch.size.area();
        double overlap = 1 - expectedMatch.maxSizeDifferenceOfActualAndExpectedMatchInPercent / 100;
        boolean sizeWithinInterval = actualArea / expectedArea > overlap && expectedArea / actualArea > overlap;
        BigDecimal actualPercent = new BigDecimal(actualArea / expectedArea).setScale(2, BigDecimal.ROUND_HALF_UP);
        AssertCollector.assertTrue(
                sizeWithinInterval,
                "actual match size differs by " + actualPercent + "% from expected match size. Threshold for passing is " + expectedMatch.maxSizeDifferenceOfActualAndExpectedMatchInPercent + "%."
        );
    }

    private int distance(Point a, Point b) {
        double x = (a.x - b.x);
        double y = (a.y - b.y);
        return (int) (x * y);
    }
}
