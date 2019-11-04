package eu.tsystems.mms.tic.testframework.layout;

import eu.tsystems.mms.tic.testframework.common.IProperties;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.internal.Constants;
import eu.tsystems.mms.tic.testframework.layout.extraction.AnnotationReader;
import eu.tsystems.mms.tic.testframework.layout.reporting.LayoutCheckContext;
import eu.tsystems.mms.tic.testframework.report.IReport;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class for handling layout checking screenshots.
 *
 * @author mibu
 */
public final class LayoutCheck {

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
        REFERENCE_PATH("reference.path", "src/test/resources/" + Constants.SCREENREFERENCES_PATH + "/reference"),
        DISTANCE_PATH("distance.path", "src/test/resources/" + Constants.SCREENREFERENCES_PATH + "/distance"),
        ACTUAL_PATH("actual.path", "src/test/resources/" + Constants.SCREENREFERENCES_PATH + "/actual"),
        USE_IGNORE_COLOR("use.ignore.color", false),
        USE_AREA_COLOR("use.area.color", false),
        PIXEL_RGB_DEVIATION_PERCENT("pixel.rgb.deviation.percent", 0),

        // Properties for the layout comparator working with
        MATCH_THRESHOLD("match.threshold", 0.95d),
        DISPLACEMENT_THRESHOLD("displacement.threshold", 5),
        INTRA_GROUPING_THRESHOLD("intra.grouping.threshold", 5),
        MINIMUM_MARKED_PIXELS("minimum.marked.pixels", 12),
        MAXIMUM_MARKED_PIXELS_RATIO("maximum.marked.pixels.ratio", 0.04d),
        MATCHING_ALGORITHM("matching.algorithm", "opencvtemplatematcher"),
        /**
         * minimalDistanceBetweenMatches
         */
        INTERNAL_PARAMETER_1("internal.parameter.1", 5),
        /**
         * minimalSizeDifferenceOfSubImages
         */
        INTERNAL_PARAMETER_2("internal.parameter.2", 10),
        /**
         * minimumSimilarMovementErrorsForDisplacementCorrection
         */
        INTERNAL_PARAMETER_3("internal.parameter.3",  0.51d),
        /**
         * distanceBetweenMultipleMatchesToProduceWarning
         */
        INTERNAL_PARAMETER_4("internal.parameter.4", 14),
        IGNORE_AMBIGUOUS_MOVEMENT("ignore.ambiguous.movement", null),
        IGNORE_MOVEMENT("ignore.movement", null),
        IGNORE_GROUP_MOVEMENT("ignore.group.movement", false),
        IGNORE_MISSING_ELEMENTS("ignore.missing.elements", null),
        IGNORE_AMBIGUOUS_MATCH("ignore.ambiguous.match", null),

        /**
         * If below 1, the value is regarded as percent threshold for erroneous pixels / all edge and text pixel. If 1 or
         * greater, it is regarded as absolute error pixel count.
         */
        //LAYOUTCHECK_TEXT_ERRORDETECTOR_ERROR_THRESHOLD("text.error.detector.error.threshold", null),
        LAYOUTCHECK_TEXT_ERRORDETECTOR_MINIMAL_LINELENGTH("text.error.detector.minimal.line.length", 25),
        LAYOUTCHECK_TEXT_ERRORDETECTOR_MINIMAL_EDGESTRENGTH("text.error.detector.minimal.edge.strength", 5),
        ;
        private final String property;
        private Object defaultValue;

        Properties(String property, Object defaultValue) {
            this.property = property;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return String.format("tt.layoutcheck.%s", property);
        }

        @Override
        public IProperties newDefault(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @Override
        public Double asDouble() {
            return PropertyManager.parser.getDoubleProperty(toString(), defaultValue);
        }
        @Override
        public Long asLong() {
            return PropertyManager.parser.getLongProperty(toString(), defaultValue);
        }
        @Override
        public Boolean asBool() {
            return PropertyManager.parser.getBooleanProperty(toString(), defaultValue);
        }
        @Override
        public String asString() { return PropertyManager.parser.getProperty(toString(), defaultValue); }
    }

    public static class MatchStep {
        public Mode mode;
        Path referenceFileName;
        Path annotatedReferenceFileName;
        Path actualFileName;
        Path distanceFileName;
        Path annotationDataFileName;
        String consecutiveTargetImageName;
        public boolean takeReferenceOnly;
        public double distance = NO_DISTANCE;
        public LayoutComparator layoutComparator;
    }

    private static final IReport report = Testerra.ioc().getInstance(IReport.class);

    /**
     * Hide Default constructor.
     */
    private LayoutCheck() {
    }

    /**
     * LayoutCheck Mode options
     *
     * @author sepr
     */
    public enum Mode {
        PIXEL, ANNOTATED
    }

    private static final double NO_DISTANCE = 0;
    private static final int RGB_DEVIATION_PERCENT = Properties.PIXEL_RGB_DEVIATION_PERCENT.asLong().intValue();
    private static final double RGB_MAX_DEVIATION = 255;
    private static final File REFERENCE_IMAGES_PATH = new File(Properties.REFERENCE_PATH.asString());
    private static final File DISTANCE_IMAGES_PATH = new File(Properties.DISTANCE_PATH.asString());
    private static final File ACTUAL_IMAGES_PATH = new File(Properties.ACTUAL_PATH.asString());

    private static HashMap<String, Integer> runCount = new HashMap<String, Integer>();

    static {
        // ensure the folders to save the images exist
        REFERENCE_IMAGES_PATH.mkdirs();
        DISTANCE_IMAGES_PATH.mkdirs();
        ACTUAL_IMAGES_PATH.mkdirs();
    }

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutCheck.class);

    /**
     *
     * @param webDriver Web driver instance, has to implement TakesScreenshot
     * @param targetImageName filename of the reference screenshot/distance image (extended by a given prefix from the
     *                        properties)
     * @return Percents of pixels that are different
     */
    public static double run(WebDriver webDriver, String targetImageName) {
        String modeString = Properties.MODE.toString().trim().toUpperCase();
        Mode mode = Mode.valueOf(modeString);

        return run(webDriver, targetImageName, mode);
    }

    /**
     *
     * @param webDriver Web driver instance, has to implement TakesScreenshot
     * @param targetImageName filename of the reference screenshot/distance image (extended by a given prefix from the
     *                        properties
     * @param mode PIXEL or ANNOTATED
     * @return Percents of pixels that are different
     */
    @Deprecated
    public static double run(WebDriver webDriver, final String targetImageName, final Mode mode) {
        if (TakesScreenshot.class.isAssignableFrom(webDriver.getClass())) {
            return pRun((TakesScreenshot) webDriver, targetImageName, mode).distance;
        } else {
            throw new RuntimeException("Passed WebDriver does not implement TakesScreenshot. WebDriver is of class " + webDriver.getClass());
        }
    }

    /**
     * Handles a screenshot action called from a test. Takes a new reference screenshot if the property
     * 'Testerra.layoutcheck.takereference' is set to true. Otherwise takes a screenshot and creates a new distance
     * image comparing the actual shown website to an existing reference screenshot.
     *
     * @param driver          Web driver instance
     * @param targetImageName filename of the reference screenshot/distance image (extended by a given prefix from the
     *                        properties)
     * @return Percents of pixels that are different (0 if reference is taken)
     */
    @Deprecated
    public static double run(final TakesScreenshot driver, final String targetImageName) {
        String modeString = Properties.MODE.toString().trim().toUpperCase();
        Mode mode = Mode.valueOf(modeString);

        return pRun(driver, targetImageName, mode).distance;
    }

    /**
     * Creates directories for the images/ screenshots and creates distance image
     *
     * @param driver          WebDriver used
     * @param targetImageName name of the target image
     * @param mode            PIXEL or ANNOTATED
     * @return distance between the images
     */
    @Deprecated
    public static double run(final TakesScreenshot driver, final String targetImageName, final Mode mode) {
        return pRun(driver, targetImageName, mode).distance;
    }

    /**
     * Matches annotations and returns
     */
    public static MatchStep matchAnnotations(
        final TakesScreenshot driver,
        final String targetImageName
    ) {
        final File screenshot = driver.getScreenshotAs(OutputType.FILE);
        return matchAnnotations(screenshot, targetImageName);
    }

    public static MatchStep matchAnnotations(
        final File screenshot,
        final String targetImageName
    ) {
        MatchStep step = prepare(screenshot, targetImageName);
        step.mode = Mode.ANNOTATED;
        if (!step.takeReferenceOnly) {
            matchAnnotations(step);
        }
        return step;
    }

    private static void matchAnnotations(final MatchStep step) {
        // read images
        String referenceAbsoluteFileName = step.referenceFileName.toAbsolutePath().toString();
        String annotatedAbsoluteFileName = step.annotatedReferenceFileName.toAbsolutePath().toString();
        String actualAbsoluteFileName = step.actualFileName.toAbsolutePath().toString();
        String distanceAbsoluteFileName = step.distanceFileName.toAbsolutePath().toString();
        String annotationDataAbsoluteFileName = step.annotationDataFileName.toAbsolutePath().toString();

        // create distance image to given reference
        LayoutComparator layoutComparator = new LayoutComparator();
        try {
            layoutComparator.compareImages(
                referenceAbsoluteFileName,
                annotatedAbsoluteFileName,
                actualAbsoluteFileName,
                distanceAbsoluteFileName,
                annotationDataAbsoluteFileName
            );
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
            throw new TesterraSystemException("Error reading images", e);
        }

        step.distance = layoutComparator.getErrorRelation();
        step.layoutComparator = layoutComparator;
    }

    /**
     * Takes reference screenshots and prepares file paths for discrete matching modes
     */
    private static MatchStep prepare(
        final File screenshot,
        final String targetImageName
    ) {
        final MatchStep step = new MatchStep();
        step.referenceFileName = Paths.get(REFERENCE_IMAGES_PATH + "/" +
            String.format(Properties.REFERENCE_NAMETEMPLATE.asString(), targetImageName)
        );
        step.annotationDataFileName = Paths.get(REFERENCE_IMAGES_PATH + "/" +
            String.format(Properties.ANNOTATIONDATA_NAMETEMPLATE.asString(), targetImageName)
        );
        step.annotatedReferenceFileName = Paths.get(REFERENCE_IMAGES_PATH + "/" +
            String.format(Properties.ANNOTATED_NAMETEMPLATE.asString(), targetImageName)
        );

        String runCountModifier = "";
        if (!runCount.containsKey(targetImageName)) {
            runCount.put(targetImageName, 1);
        } else {
            Integer newCount = runCount.get(targetImageName) + 1;
            runCount.put(targetImageName, newCount);
            runCountModifier = newCount.toString();
        }

        step.takeReferenceOnly = Properties.TAKEREFERENCE.asBool();
        if (step.takeReferenceOnly) {
            // take reference screenshot
            try {
                FileUtils.copyFile(screenshot, step.referenceFileName.toFile());
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                throw new TesterraSystemException("Error when saving reference screenshot.", e);
            }
            LOGGER.info(String.format("Saved reference screenshot at '%s'.", step.referenceFileName.toString()));
        } else {
            step.consecutiveTargetImageName = targetImageName + runCountModifier;
            step.actualFileName = Paths.get(
                ACTUAL_IMAGES_PATH + "/" +
                String.format(Properties.ACTUAL_NAMETEMPLATE.asString(), step.consecutiveTargetImageName)
            );

            try {
                FileUtils.copyFile(screenshot, step.actualFileName.toFile());
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                throw new TesterraSystemException("Error when saving screenshot.", e);
            }
            LOGGER.debug(String.format("Saved actual screenshot at '%s'.", step.actualFileName.toString()));

            // create distance file name
            step.distanceFileName = Paths.get(DISTANCE_IMAGES_PATH + "/" +
                String.format(Properties.DISTANCE_NAMETEMPLATE.asString(), step.consecutiveTargetImageName)
            );
        }

        return step;
    }

    /**
     * Matches image pixels and returns an absolute distance value
     */
    public static MatchStep matchPixels(final TakesScreenshot driver, final String targetImageName) {
        final File screenshot = driver.getScreenshotAs(OutputType.FILE);
        return matchPixels(screenshot, targetImageName);
    }

    public static MatchStep matchPixels(final File screenshot, final String targetImageName) {
        final MatchStep step = prepare(screenshot, targetImageName);
        step.mode = Mode.PIXEL;
        if (!step.takeReferenceOnly) {
            matchPixels(step);
        }
        return step;
    }

    private static void matchPixels(final MatchStep step) {
        try {
            // read images
            File refFile = step.referenceFileName.toFile();
            File actualFile = step.actualFileName.toFile();

            if (!refFile.exists()) {
                throw new FileNotFoundException(step.referenceFileName.toString());
            }
            if (!actualFile.exists()) {
                throw new FileNotFoundException(step.actualFileName.toString());
            }

            final BufferedImage referenceImage = ImageIO.read(refFile);
            final BufferedImage actualImage = ImageIO.read(actualFile);

            final boolean useIgnoreColor = Properties.USE_IGNORE_COLOR.asBool();

            // create distance image to given reference
            step.distance = generateDistanceImage(
                referenceImage,
                actualImage,
                step.distanceFileName.toAbsolutePath().toString(),
                useIgnoreColor
            );
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new TesterraSystemException("Error reading images", e);
        }
    }

    /**
     * (private method) Creates directories for the images/ screenshots and creates distance image
     *
     * @param driver          WebDriver used
     * @param targetImageName name of the target image
     * @param mode            PIXEL or ANNOTATED
     * @return distance between the images
     */
    private static synchronized MatchStep pRun(
        final TakesScreenshot driver,
        final String targetImageName,
        final Mode mode
    ) {
        LOGGER.debug("Starting ScreenReferencer in " + mode.name() + " mode.");
        final File screenshot = driver.getScreenshotAs(OutputType.FILE);
        final MatchStep step = prepare(screenshot, targetImageName);

        if (step.takeReferenceOnly == false) {
            step.mode = mode;
            switch (mode) {
                case PIXEL:
                    matchPixels(step);
                    toReport(step);
                    break;
                case ANNOTATED:
                    matchAnnotations(step);
                    toReport(step);
                    break;
                default:
                    LOGGER.error("Mode" + mode.name() + "not supported");
                    throw new TesterraSystemException("Mode " + mode.name() + " not supported.");
            }
        }

        return step;
    }

    /**
     * Returns the color of a pixel at a certain position of the image
     *
     * @param image with a certain colored pixel
     * @param x     Position of the pixel
     * @param y     Position of the pixel
     * @return color code of the pixel
     */
    private static int getColorOfPixel(BufferedImage image, int x, int y) {
        return image.getRGB(x, y);
    }

    /**
     * Creates an image showing the differences of the given images and calculates the difference between the images in
     * percent.
     *
     * @param expectedImage  The expected image
     * @param actualImage    The actual image
     * @param resultFilename Filename to the save the image containing the differences
     * @return Percents of pixels that are different
     */
    private static double generateDistanceImage(
        final BufferedImage expectedImage,
        final BufferedImage actualImage,
        final String resultFilename,
        final boolean useIgnoreColor
    ) {
        // for counting the pixels that are different
        int pixelsInError = 0;
        int noOfIgnoredPixels = 0;
        // calculate the size of the distance image and create an empty image
        final Dimension distanceImageSize = calculateMaxImageSize(expectedImage, actualImage);
        final BufferedImage distanceImage = new BufferedImage(
                distanceImageSize.width, distanceImageSize.height,
                expectedImage.getType());

        Dimension expectedImageDimension = new Dimension(expectedImage.getWidth(), expectedImage.getHeight());
        Dimension actualImageDimension = new Dimension(actualImage.getWidth(), actualImage.getHeight());

        if (!actualImageDimension.equals(expectedImageDimension)) {
            NonFunctionalAssert.fail("The actual screenshot has a different size than the reference. Actual = " +
                    actualImageDimension +
                    ", Reference = " +
                    expectedImageDimension +
                    ".");
        }

        List<Rectangle> markedRectangles = null;
        boolean useExplicitRectangles = Properties.USE_AREA_COLOR.asBool();
        if (!useIgnoreColor && useExplicitRectangles) {
            AnnotationReader annotationReader = new AnnotationReader();
            markedRectangles = annotationReader.readAnnotationDimensions(expectedImage);
            if (markedRectangles == null) {
                LOGGER.warn("No marked areas were found. This could be intentional.");
            }
        }

        if (markedRectangles == null) {
            markedRectangles = new ArrayList<Rectangle>();
            Rectangle rectangle = new Rectangle(0, 0, distanceImageSize.width, distanceImageSize.height);
            markedRectangles.add(rectangle);
        } else {
            for (int currentY = 0; currentY < distanceImage.getHeight(); currentY++) {
                for (int currentX = 0; currentX < distanceImage.getWidth(); currentX++) {
                    boolean pixelIsInsideExpectedImage = isPixelInImageBounds(expectedImage, currentX, currentY);
                    if (pixelIsInsideExpectedImage) {
                        int rgb = expectedImage.getRGB(currentX, currentY);
                        Color color = new Color(rgb);
                        color = color.darker().darker();
                        distanceImage.setRGB(currentX, currentY, color.getRGB());
                    } else {
                        distanceImage.setRGB(currentX, currentY, Color.BLUE.getRGB());
                    }
                }
            }
        }

        int ignoreColor = getColorOfPixel(expectedImage, 0, 0);

        for (Rectangle rectangle : markedRectangles) {
            for (int currentY = rectangle.y; currentY < rectangle.y + rectangle.height; currentY++) {
                for (int currentX = rectangle.x; currentX < rectangle.x + rectangle.width; currentX++) {
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

                        //

                        if (!ignoredPixel) {
                            boolean match = doRGBsMatch(expectedRgb, actualImageRGB);
                            if (!match) {
                                // mark the current pixel as error by painting it red
                                distanceImage.setRGB(currentX, currentY, Color.RED.getRGB());
                                pixelsInError++;
                            }
                        }

                        // count the ignored pixels for calculating
                        if (useIgnoreColor && expectedRgb == ignoreColor) {
                            noOfIgnoredPixels++;
                        }
                    } else {
                        // this pixel is not inside one or the other image - mark it, but not as error
                        distanceImage.setRGB(currentX, currentY, Color.BLUE.getRGB());
                    }
                }
            }
        }

        try {
            // write image to given file
            ImageIO.write(distanceImage, "PNG", new File(resultFilename));
        } catch (IOException ioe) {
            LOGGER.error(
                    String.format("An error occurred while trying to persist image to '%s'.", resultFilename),
                    ioe);
        }

        int totalPixels = 0;
        for (Rectangle rectangleToCompare : markedRectangles) {
            totalPixels += rectangleToCompare.height * rectangleToCompare.width;
        }

        // calculate and return the percentage number of pixels in error
        return ((double) pixelsInError / (totalPixels - noOfIgnoredPixels)) * 100;
    }

    public static boolean doRGBsMatch(int expectedRgb, int actualImageRGB) {
        if (expectedRgb == actualImageRGB) {
            return true;
        }

        if (RGB_DEVIATION_PERCENT > 0) {
            Color expectedColor = new Color(expectedRgb);
            Color actualColor = new Color(actualImageRGB);

            int percentR = (int) (100 * (Math.abs(expectedColor.getRed() - actualColor.getRed())) / RGB_MAX_DEVIATION);
            int percentG = (int) (100 * (Math.abs(expectedColor.getGreen() - actualColor.getGreen())) / RGB_MAX_DEVIATION);
            int percentB = (int) (100 * (Math.abs(expectedColor.getBlue() - actualColor.getBlue())) / RGB_MAX_DEVIATION);

//            LOGGER.info("RGB deviation percent: " + percentR + "/" + percentG + "/" + percentB);
            if (percentR <= RGB_DEVIATION_PERCENT && percentG <= RGB_DEVIATION_PERCENT && percentB <= RGB_DEVIATION_PERCENT) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the sizes thats result from the maximum sizes of both pictures.
     *
     * @param expectedImage The expected image
     * @param actualImage   The actual image
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
     * @param x     X-coordinate of the pixel
     * @param y     Y-coordinate of the pixel
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
        final String name = step.consecutiveTargetImageName;
        final Path referenceScreenshotPath = step.referenceFileName;
        final Path actualScreenshotPath = step.actualFileName;
        final Path distanceScreenshotPath = step.distanceFileName;

        LayoutCheckContext context = new LayoutCheckContext();
        context.image = name;
        context.mode = step.mode.name();
        context.distance = step.distance;
        // Always copy the reference image
        context.expectedScreenshot = report.provideScreenshot(referenceScreenshotPath.toFile(), IReport.Mode.COPY);
        context.actualScreenshot = report.provideScreenshot(actualScreenshotPath.toFile(), IReport.Mode.MOVE);
        context.distanceScreenshot = report.provideScreenshot(distanceScreenshotPath.toFile(), IReport.Mode.MOVE);
        context.distanceScreenshot.meta().put("Distance", Double.toString(step.distance));
        if (step.annotatedReferenceFileName!=null) {
            final Path annotatedReferenceScreenshotPath = step.annotatedReferenceFileName;
            context.annotatedScreenshot = report.provideScreenshot(annotatedReferenceScreenshotPath.toFile(), IReport.Mode.MOVE);
        }
        MethodContext methodContext = ExecutionContextController.getCurrentMethodContext();
        methodContext.customContexts.add(context);
    }
}
