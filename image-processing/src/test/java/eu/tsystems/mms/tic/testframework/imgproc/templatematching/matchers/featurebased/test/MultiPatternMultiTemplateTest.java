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
import eu.tsystems.mms.tic.testframework.imgproc.templatematching.matchers.featurebased.Parameters;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Stream;

public class MultiPatternMultiTemplateTest extends AbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiPatternMultiTemplateTest.class);

    List<String> scenes = new LinkedList<>();
    List<String> templates = new LinkedList<>();
    private final Map<String, String> expectedFound = new HashMap<>();
    private int parallelism = Runtime.getRuntime().availableProcessors() / 2;

    @Test
    public void multiPatternMultiTemplateTest() {
        scenes.add("screenshot-pattern-none-huge.png");
        scenes.add("screenshot-pattern-quadrop-huge.png");
        scenes.add("screenshot-pattern-punkt-huge.png");
        scenes.add("screenshot-pattern-angel-huge.png");
        scenes.add("screenshot-pattern-chess-huge.png");
        scenes.add("screenshot-pattern-hahnentritt-huge.png");

        templates.add("pattern-quadrop.png");
        templates.add("pattern-chess.png");
        templates.add("pattern-angel.png");
        templates.add("pattern-hahnentritt.png");
        templates.add("pattern-punkt.png");

        expectedFound.put("pattern-quadrop.png", "screenshot-pattern-quadrop-huge.png");
        expectedFound.put("pattern-punkt.png", "screenshot-pattern-punkt-huge.png");
        expectedFound.put("pattern-chess.png", "screenshot-pattern-chess-huge.png");
        expectedFound.put("pattern-angel.png", "screenshot-pattern-angel-huge.png");
        // expectedFound.put("pattern-hahnentritt.png", "screenshot-pattern-hahnentritt-huge.png");

        run(scenes, templates);
    }

    private void run(List<String> scenes, List<String> templates) {
        Stream<Parameters> params = Stream.of(
                Parameters.getDefault()
        );
        params.forEach(
                param -> {
                    Matcher matcher = new Matcher(param);
                    processConcurrently(
                            permute(templates, scenes),
                            templateAndScene -> {
                                String template = templateAndScene.getValue0();
                                String scene = templateAndScene.getValue1();
                                Optional<Match> match = matcher.match(
                                        RESOURCES_DIR.resolve(template),
                                        RESOURCES_DIR.resolve(scene));
                                AssertCollector.assertEquals(
                                        match.isPresent(),
                                        expectedFound.getOrDefault(template, "").equals(scene),
                                        "wrong matchstate! searching " + template + " in " + scene);
                                //LoggingDispatcher.linkThread(Thread.currentThread());
                            },
                            parallelism
                    );
                }
        );
    }

    private <T> Stream<Pair<T, T>> permute(Collection<T> a, Collection<T> b) {
        Set<Pair<T, T>> permutations = new HashSet<>();
        for (T ca : a) {
            for (T cb : b) {
                permutations.add(new Pair<>(ca, cb));
            }
        }
        return permutations.stream();
    }
}
