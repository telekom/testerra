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

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.imgproc.ImgprocUtils;
import eu.tsystems.mms.tic.testframework.imgproc.templatematching.strategy.MatchingAlgorithm;
import eu.tsystems.mms.tic.testframework.imgproc.utils.OpenCvInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Matcher implements MatchingAlgorithm<Match> {
    private static ThreadLocal<Parameters> params = new ThreadLocal<>();
    private final Parameters initialParameters;
    private static Logger LOGGER = LoggerFactory.getLogger(Matcher.class);
    public static final ZonedDateTime firstInitTimestamp = ImgprocUtils.now();

    static {
        OpenCvInitializer.initOnce();
    }

    public Matcher(Parameters parameters) {
        this.initialParameters = parameters;
    }

    public Matcher() {
        this(Parameters.getDefault());
    }

    // this excpetion should never fly, if it does please contact author
    static Parameters params() {
        try {
            return params.get();
        } catch (Exception e) {
            throw new TesterraSystemException("threadlocal parameters for template matcher not set", e);
        }
    }

    private MatchResult pMatch(Path templatePath, Path scenePath) throws Exception {
        // double blur_size = Math.min(template.getImage().size().width, template.getImage().size().height) / 40;
        LOGGER.info("matching template " + templatePath + " to scene " + scenePath);
        TemplateImage template = new TemplateImage(templatePath);
        SceneImage scene = new SceneImage(scenePath);

        template.scale(params().UPSCALE_FACTOR);
        scene.scale(params().UPSCALE_FACTOR);
        template.blur(params().BLUR_SIZE * params().UPSCALE_FACTOR);
        scene.blur(params().BLUR_SIZE * params().UPSCALE_FACTOR);
        template.detectFeatures();
        scene.detectFeatures();
        template.computeDescriptors();
        scene.computeDescriptors();

        MatchResult result = MatchResult.calculate(template, scene);
        if (result.match.isPresent()) {
            Match match = result.match.get();
            LOGGER.info("found match at " + match.getCenter() + " matched " + match.matches.size() + " pixels. Time: " + result.executionTime);
            result.output.writeDebugPicture(
                    Paths.get("target/surefire-reports/feature-based-matcher/"
                            + ImgprocUtils.FORMATTER_FILENAME_FRIENDLY_WITH_MILLISECONDS.format(Matcher.firstInitTimestamp)
                            + "/"
                            + ImgprocUtils.timestamp()
                            + "_"
                            + result.executionTime
                            + "_"
                            + params()
                            + "_"
                            + match.getCenter().toString()
                            + "_"
                            + match.getRect().size()
                            + ".png")
            );
        } else {
            LOGGER.info("no match found");
        }
        return result;
    }

    /**
     * threadsafe
     *
     * @param template
     * @param scene
     * @return
     * @throws Exception
     */
    @Override
    public Optional<Match> match(Path template, Path scene) {
        Optional<Match> match = Optional.empty();
        Callable<MatchResult> callable = () -> {
            params.set(initialParameters);
            return pMatch(template, scene);
        };
        try {
            FutureTask<MatchResult> task = new FutureTask<>(callable);
            task.run();
            MatchResult matchResult = task.get();
            match = matchResult.match;
            matchResult.release();
        } catch (InterruptedException ie) {
            throw new TesterraSystemException("Unexpected Error. Possible causes: out of memory, matcher process killed externally, actual bug", ie);
        } catch (ExecutionException ee) {
            LOGGER.error("error while searching for template " + template + " in scene " + scene, ee.getCause());
        }
        return match;
    }
}
