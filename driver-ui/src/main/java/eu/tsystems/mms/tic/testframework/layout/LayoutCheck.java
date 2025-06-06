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
package eu.tsystems.mms.tic.testframework.layout;

import eu.tsystems.mms.tic.testframework.common.IProperties;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.execution.testng.OptionalAssert;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.context.LayoutCheckContext;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

/**
 * Utility class for handling layout checking screenshots.
 *
 * @author mibu
 */
public final class LayoutCheck implements PropertyManagerProvider, AssertProvider {

    public enum Properties implements IProperties {
        MODE("mode", "pixel"),
        TAKEREFERENCE("takereference", false),
        // if true, will use non-functional asserts
        //LAYOUTCHECK_ASSERT_INFO_MODE("assert.info.mode", null),
        REFERENCE_NAMETEMPLATE("reference.nametemplate", "Reference%s.png"),
        ANNOTATED_NAMETEMPLATE("annotated.nametemplate", "ReferenceAnnotated%s.png"),
        ANNOTATIONDATA_NAMETEMPLATE("annotationdata.nametemplate", "Reference%s_data.json"),
        ACTUAL_NAMETEMPLATE("actual.nametemplate", "Actual%s.png"),
        DISTANCE_NAMETEMPLATE("distance.nametemplate", "Distance%s.png"),
        REFERENCE_PATH("reference.path", "src/test/resources/screenreferences/reference"),
        DISTANCE_PATH("distance.path", "src/test/resources/screenreferences/distance"),
        ACTUAL_PATH("actual.path", "src/test/resources/screenreferences/actual"),
        USE_IGNORE_COLOR("use.ignore.color", false),
        PIXEL_RGB_DEVIATION_PERCENT("pixel.rgb.deviation.percent", 0.0),
        PIXEL_COUNT_HARD_ASSERTION("pixel.count.hard.assertion", false),

        ;
        private final String property;
        private final Object defaultValue;

        Properties(String property, Object defaultValue) {
            this.property = property;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return String.format("tt.layoutcheck.%s", property);
        }

        @Override
        public Object getDefault() {
            return defaultValue;
        }
    }

    public static class MatchStep {
        Path referenceFileName;
        Path actualFileName;
        Path distanceFileName;
        Path annotationDataFileName;
        Dimension referenceFileDimension;
        Dimension actualFileDimension;
        String consecutiveTargetImageName;
        public boolean takeReferenceOnly;
        public double distance = NO_DISTANCE;

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutCheck.class);

    private static final Report report = Testerra.getInjector().getInstance(Report.class);

    private static final double NO_DISTANCE = 0;
    private static final double RGB_DEVIATION_PERCENT = Properties.PIXEL_RGB_DEVIATION_PERCENT.asDouble();
    private static final double RGB_MAX_DEVIATION = 255;
    private static final HashMap<String, Integer> runCount = new HashMap<>();
    // base dir for all layoutcheck images, will be filled in findRealBaseDir method
    private static Path baseDir = null;

    private LayoutCheck() {
    }

    /**
     * Takes reference screenshots and prepares file paths for discrete matching modes
     */
    private static MatchStep prepare(
            final Path screenshot,
            final String targetImagePath
    ) {
        if (baseDir == null) {
            findRealBaseDir();
        }

        final MatchStep step = new MatchStep();
        step.takeReferenceOnly = Properties.TAKEREFERENCE.asBool();

        Path referenceImagesDir = baseDir.resolve(Properties.REFERENCE_PATH.asString());
        Path actualImagesDir = baseDir.resolve(Properties.ACTUAL_PATH.asString());
        Path distanceImagesDir = baseDir.resolve(Properties.DISTANCE_PATH.asString());

        // Check targetImageName for subfolders
        Path targetImage = Paths.get(targetImagePath);
        String targetImageName = targetImage.getFileName().toString();
        if (targetImage.getParent() != null) {
            referenceImagesDir = referenceImagesDir.resolve(targetImage.getParent());
            actualImagesDir = actualImagesDir.resolve(targetImage.getParent());
            distanceImagesDir = distanceImagesDir.resolve(targetImage.getParent());
        }

        FileUtils.createDirectoriesSafely(referenceImagesDir);
        FileUtils.createDirectoriesSafely(actualImagesDir);
        FileUtils.createDirectoriesSafely(distanceImagesDir);

        step.referenceFileName = referenceImagesDir.resolve(String.format(
                PROPERTY_MANAGER.getProperty(Properties.REFERENCE_NAMETEMPLATE, "Reference%s.png"),
                targetImageName
        ));

        String runCountModifier = "";
        if (!runCount.containsKey(targetImageName)) {
            runCount.put(targetImageName, 1);
        } else {
            Integer newCount = runCount.get(targetImageName) + 1;
            runCount.put(targetImageName, newCount);
            runCountModifier = String.format("-%03d", newCount);
        }

        if (step.takeReferenceOnly) {
            // create reference image
            try {
                Files.copy(screenshot, step.referenceFileName, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                throw new SystemException("Error when saving reference image.", e);
            }
            LOGGER.info(String.format("Saved reference image at '%s'.", step.referenceFileName.toString()));
        } else {
            step.consecutiveTargetImageName = targetImageName + runCountModifier;

            step.actualFileName = actualImagesDir.resolve(
                    String.format(Properties.ACTUAL_NAMETEMPLATE.asString(), step.consecutiveTargetImageName)
            );

            try {
                Files.copy(screenshot, step.actualFileName, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                throw new SystemException("Error when saving image.", e);
            }
            LOGGER.debug(String.format("Saved actual image at '%s'.", step.actualFileName.toString()));

            // create distance file name
            step.distanceFileName = distanceImagesDir.resolve(
                    String.format(Properties.DISTANCE_NAMETEMPLATE.asString(), step.consecutiveTargetImageName)
            );
        }

        return step;
    }

    /**
     * Matches image pixels and returns an absolute distance value
     */
    public static MatchStep matchPixels(WebDriver webDriver, final String targetImageName) {
        Screenshot screenshot = new Screenshot();
        UITestUtils.takeWebDriverScreenshotToFile(webDriver, screenshot.getScreenshotFile());
        return matchPixels(screenshot.getScreenshotFile(), targetImageName);
    }

    public static MatchStep matchPixels(final File screenshot, final String targetImageName) {
        final MatchStep step = prepare(screenshot.toPath(), targetImageName);
        if (!step.takeReferenceOnly) {
            matchPixels(step);
        }
        return step;
    }

    private static void matchPixels(final MatchStep matchStep) {
        try {
            // read images
            Path refFile = matchStep.referenceFileName;
            Path actualFile = matchStep.actualFileName;

            if (!Files.exists(refFile)) {
                throw new FileNotFoundException(matchStep.referenceFileName.toString());
            }
            if (!Files.exists(actualFile)) {
                throw new FileNotFoundException(matchStep.actualFileName.toString());
            }

            final BufferedImage referenceImage = ImageIO.read(Files.newInputStream(refFile));
            final BufferedImage actualImage = ImageIO.read(Files.newInputStream(actualFile));

            matchStep.referenceFileDimension = new Dimension(referenceImage.getWidth(), referenceImage.getHeight());
            matchStep.actualFileDimension = new Dimension(actualImage.getWidth(), actualImage.getHeight());

            final boolean useIgnoreColor = Properties.USE_IGNORE_COLOR.asBool();

            // create distance image to given reference
            matchStep.distance = generateDistanceImage(
                    referenceImage,
                    actualImage,
                    matchStep.distanceFileName,
                    useIgnoreColor
            );

        } catch (Exception e) {
            throw new LayoutCheckException(matchStep, e);
        }
    }

    /**
     * Returns the color of a pixel at a certain position of the image
     *
     * @param image with a certain colored pixel
     * @param x Position of the pixel
     * @param y Position of the pixel
     * @return color code of the pixel
     */
    private static int getColorOfPixel(BufferedImage image, int x, int y) {
        return image.getRGB(x, y);
    }

    /**
     * Creates an image showing the differences of the given images and calculates the difference between the images in
     * percent. Also calculates the percentage of pixels that are incorrect based on the property 'tt.layoutcheck.pixel.count.hard.assertion'.
     *
     * @param expectedImage The expected image
     * @param actualImage The actual image
     * @param resultFilename Filename to the save the image containing the differences
     * @return Percents of pixels that are different
     */
    private static double generateDistanceImage(
            final BufferedImage expectedImage,
            final BufferedImage actualImage,
            final Path resultFilename,
            final boolean useIgnoreColor
    ) {
        // for counting the pixels that are different
        int pixelsInError = 0;
        int noOfIgnoredPixels = 0;
        // for counting the pixels that are just in one image
        int noOfExclusivePixels = 0;
        // calculate the size of the distance image and create an empty image
        final Dimension distanceImageSize = calculateMaxImageSize(expectedImage, actualImage);
        final BufferedImage distanceImage = new BufferedImage(
                distanceImageSize.width, distanceImageSize.height,
                expectedImage.getType());

        int ignoreColor = getColorOfPixel(expectedImage, 0, 0);

        for (int currentY = 0; currentY < distanceImageSize.height; currentY++) {
            for (int currentX = 0; currentX < distanceImageSize.width; currentX++) {
                boolean pixelIsInsideExpectedImage = isPixelInImageBounds(expectedImage, currentX, currentY);
                boolean pixelIsInsideActualImage = isPixelInImageBounds(actualImage, currentX, currentY);

                if (pixelIsInsideExpectedImage) {
                    // draw every pixel that is available in the expected image
                    distanceImage.setRGB(currentX, currentY,
                            expectedImage.getRGB(currentX, currentY));
                }

                if (pixelIsInsideExpectedImage && pixelIsInsideActualImage) {
                    // if the pixel color at x,y is not equal and the pixel is not marked as 'to ignore'
                    int expectedRgb = expectedImage.getRGB(currentX, currentY);
                    int actualImageRGB = actualImage.getRGB(currentX, currentY);

                    boolean ignoredPixel = useIgnoreColor && expectedRgb == ignoreColor;
                    if (!ignoredPixel) {
                        boolean match = doRGBsMatch(expectedRgb, actualImageRGB);
                        if (!match) {
                            // mark the current pixel as error by painting it red
                            distanceImage.setRGB(currentX, currentY, Color.RED.getRGB());
                            pixelsInError++;
                        }
                    } else {
                        // count the ignored pixels for calculating
                        noOfIgnoredPixels++;
                    }
                } else {
                    // this pixel is not inside one or the other image - mark it, but not as error
                    noOfExclusivePixels++;
                    distanceImage.setRGB(currentX, currentY, Color.BLUE.getRGB());
                }
            }
        }

        try {
            // Write image to given file
            // Note:
            //      The distance image needs to be created first in a temp dir and moved then to the destination dir
            //      Cause: Executing a 'gradle test' with option '-p <project-dir>' causes a mismatch of working directory. It is still the project root dir,
            //      but the subdirectory is needed. 'org.apache.commons.io.FileUtil.FileUtils' can handle this, but not 'ImageIO'.
            File tempDistanceImage = new FileUtils().createTempFileName(resultFilename.getFileName().toString());
            ImageIO.write(distanceImage, "PNG", tempDistanceImage);
            if (resultFilename.toFile().exists()) {
                resultFilename.toFile().delete();
            }
            FileUtils.moveFile(tempDistanceImage, resultFilename.toFile());
        } catch (IOException ioe) {
            LOGGER.error(
                    String.format("An error occurred while trying to persist image to '%s'.", resultFilename),
                    ioe);
        }

        int totalPixels = distanceImageSize.width * distanceImageSize.height;

        // calculate and return the percentage number of pixels in error
        double result_rgb = ((double) pixelsInError / (totalPixels - noOfExclusivePixels - noOfIgnoredPixels)) * 100;
        double result_size = ((double) noOfExclusivePixels / totalPixels) * 100;
        double result = result_rgb;

        boolean pixelCountHardAssertion = Properties.PIXEL_COUNT_HARD_ASSERTION.asBool();
        if (pixelCountHardAssertion) {
            result += result_size;
        }

        // Just for debug log
        Dimension expectedImageDimension = new Dimension(expectedImage.getWidth(), expectedImage.getHeight());
        Dimension actualImageDimension = new Dimension(actualImage.getWidth(), actualImage.getHeight());
        LOGGER.debug("Raw results of pixel check: \n" +
                        "Dimension actual image: {}\n" +
                        "Dimension expected image: {}\n" +
                        "Number of total pixel: {}\n" +
                        "Number of ignored pixel: {}\n" +
                        "Number of pixel in errors: {}\n" +
                        "Result of matching pixels: {}\n" +
                        "Result of matching size: {}\n" +
                        "Result of matching: {}"
                , actualImageDimension, expectedImageDimension, totalPixels, noOfIgnoredPixels, pixelsInError, result_rgb, result_size, result);
        return result;
    }

    private static boolean doRGBsMatch(int expectedRgb, int actualImageRGB) {
        if (expectedRgb == actualImageRGB) {
            return true;
        }

        if (RGB_DEVIATION_PERCENT > 0.0) {
            Color expectedColor = new Color(expectedRgb);
            Color actualColor = new Color(actualImageRGB);

            double percentR = 100 * (Math.abs(expectedColor.getRed() - actualColor.getRed())) / RGB_MAX_DEVIATION;
            double percentG = 100 * (Math.abs(expectedColor.getGreen() - actualColor.getGreen())) / RGB_MAX_DEVIATION;
            double percentB = 100 * (Math.abs(expectedColor.getBlue() - actualColor.getBlue())) / RGB_MAX_DEVIATION;
//            LOGGER.debug("Current RGB deviation:\n" +
//                    "Red: {}%\n" +
//                    "Blue: {}%\n" +
//                    "Green: {}%\n",
//                    percentR, percentB, percentG);
            if (percentR <= RGB_DEVIATION_PERCENT && percentG <= RGB_DEVIATION_PERCENT && percentB <= RGB_DEVIATION_PERCENT) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the sizes that result from the maximum sizes of both pictures.
     *
     * @param expectedImage The expected image
     * @param actualImage The actual image
     * @return Calculated maximum size of the images
     */
    private static Dimension calculateMaxImageSize(
            final BufferedImage expectedImage,
            final BufferedImage actualImage
    ) {
        return new Dimension(
                Math.max(expectedImage.getWidth(), actualImage.getWidth()),
                Math.max(expectedImage.getHeight(), actualImage.getHeight())
        );
    }

    /**
     * Determines whether a pixel is within the bounds of an image.
     *
     * @param image The image
     * @param x X-coordinate of the pixel
     * @param y Y-coordinate of the pixel
     * @return true, if the pixel is within the images bounds, otherwise false
     */
    private static boolean isPixelInImageBounds(
            final BufferedImage image,
            final int x,
            final int y
    ) {
        return (image.getWidth() > x) && (image.getHeight() > y);
    }

    public static void toReport(final MatchStep step) {
        if (step == null) {
            LOGGER.warn("Cannot add layout check to report.");
            return;
        }
        IExecutionContextController contextController = Testerra.getInjector().getInstance(IExecutionContextController.class);
        final String imageName = step.consecutiveTargetImageName;
        final Path referenceScreenshotPath = step.referenceFileName;
        final Path actualScreenshotPath = step.actualFileName;
        final Path distanceScreenshotPath = step.distanceFileName;
        LayoutCheckContext context = new LayoutCheckContext();
        context.image = imageName;

        // For readable report
        context.distance = new BigDecimal(step.distance).setScale(2, RoundingMode.HALF_UP).doubleValue();
        // Always copy the reference image
        context.expectedScreenshot = report.provideScreenshot(referenceScreenshotPath.toFile(), Report.FileMode.COPY);
        context.expectedScreenshot.getMetaData().put(Screenshot.MetaData.TITLE, "Expected image");
        context.actualScreenshot = report.provideScreenshot(actualScreenshotPath.toFile(), Report.FileMode.MOVE);
        context.actualScreenshot.getMetaData().put(Screenshot.MetaData.TITLE, "Actual image");
        context.distanceScreenshot = report.provideScreenshot(distanceScreenshotPath.toFile(), Report.FileMode.MOVE);
        context.distanceScreenshot.getMetaData().put("Distance", Double.toString(step.distance));
        context.distanceScreenshot.getMetaData().put(Screenshot.MetaData.TITLE, "Difference image");

        if (!isMatchDimensions(step)) {
            // Add LayoutCheckContext for dimension check assertion
            contextController.getCurrentMethodContext().ifPresent(methodContext -> {
                // The current LayoutCheckContexts needs to be cloned here, otherwise the linking of ErrorContext and
                // LayoutCheckContext (for dimension check and for pixel check) is working with identical objects
                methodContext.addLayoutCheckContext(context.clone());
            });
            OptionalAssert.fail(
                    String.format(
                            "The actual image (width=%dpx, height=%dpx) has a different size than the reference image (width=%dpx, height=%dpx)",
                            step.actualFileDimension.width,
                            step.actualFileDimension.height,
                            step.referenceFileDimension.width,
                            step.referenceFileDimension.height
                    )
            );
        }

        // Add LayoutCheckContext for pixel distance assertion
        contextController.getCurrentMethodContext().ifPresent(methodContext -> {
            methodContext.addLayoutCheckContext(context);
        });
    }

    public static boolean isMatchDimensions(final MatchStep step) {
        return step.actualFileDimension.equals(step.referenceFileDimension);
    }

    /**
     * Check the layout of the current browser window using a reference image.
     *
     * @param webDriver WebDriver instance
     * @param targetImageName The name of the reference image
     * @param confidenceThreshold A value that defines a threshold for the layout check
     */
    public static void assertScreenshot(WebDriver webDriver, String targetImageName, double confidenceThreshold) {
        final String assertMessage = String.format("pixel distance (%%) of WebDriver screenshot to image '%s'", targetImageName);

        LayoutCheck.MatchStep matchStep = LayoutCheck.matchPixels(webDriver, targetImageName);
        assertWithLayoutCheck(matchStep, confidenceThreshold, assertMessage);
    }

    /**
     * Check the layout of an image file using a reference image.
     *
     * @param image The actual image file
     * @param targetImageName The name of the reference image
     * @param confidenceThreshold A value that defines a threshold for the layout check
     */
    public static void assertImage(File image, String targetImageName, double confidenceThreshold) {
        final String assertMessage = String.format("pixel distance (%%) of '%s' to image '%s'", image.getName(), targetImageName);

        LayoutCheck.MatchStep matchStep = LayoutCheck.matchPixels(image, targetImageName);
        assertWithLayoutCheck(matchStep, confidenceThreshold, assertMessage);
    }

    private static void assertWithLayoutCheck(MatchStep matchStep, double confidenceThreshold, String assertMessage) {
        // Check for 2 decimals of % value is enough --> Readable assertion message
        BigDecimal distance = new BigDecimal(matchStep.distance).setScale(2, RoundingMode.HALF_UP);
        BigDecimal threshold = new BigDecimal(confidenceThreshold);
        if (distance.compareTo(threshold) <= 0) {
            return;
        }
        try {
            // In case of optional or collected assertions
            if (!matchStep.takeReferenceOnly) {
                LayoutCheck.toReport(matchStep);
            }
            ASSERT.assertLowerEqualThan(distance, threshold, assertMessage);
        } catch (LayoutCheckException e) {
            matchStep = e.getMatchStep();
            LayoutCheck.toReport(matchStep);
            throw e;
        } catch (Throwable t) {
            // Needed for assertion errors
            LayoutCheck.toReport(matchStep);
            throw t;
        }
    }

    /**
     * Try to find out the real base path with the help of the default property file.
     * This is needed because in case of Gradle runs in multi-module projects the user.dir is the project root
     * but all layoutcheck images will stored in the module directory.
     * Default location is project/(submodule)/src/test/resources/...
     */
    private static void findRealBaseDir() {
        baseDir = Path.of(System.getProperty("user.dir"));
        try {
            Path testFile = new FileUtils().getLocalOrResourceFile(PropertyManager.TEST_PROPERTIES).toPath();
            Path resourcePath = Path.of("build", "resources");
            if (FileUtils.findIndexOfPath(testFile, resourcePath) < 0) {
                return;
            }
            int segmentIndex = FileUtils.findIndexOfPath(testFile, resourcePath);
            baseDir = baseDir.getRoot().resolve(testFile.subpath(0, segmentIndex));
        } catch (IOException e) {
            LOGGER.warn("Cannot find real base dir, use default", e.getMessage());
        }
    }
}
