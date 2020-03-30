
package eu.tsystems.mms.tic.testframework.layout;

import eu.tsystems.mms.tic.testframework.annotator.AnnotationContainer;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.layout.core.DistanceGraphInterpreter;
import eu.tsystems.mms.tic.testframework.layout.core.LayoutElement;
import eu.tsystems.mms.tic.testframework.layout.extraction.AnnotationReader;
import eu.tsystems.mms.tic.testframework.layout.matching.GraphBasedTemplateMatcher;
import eu.tsystems.mms.tic.testframework.layout.matching.LayoutMatch;
import eu.tsystems.mms.tic.testframework.layout.matching.TemplateMatcher;
import eu.tsystems.mms.tic.testframework.layout.matching.detection.AmbiguousMatchDetector;
import eu.tsystems.mms.tic.testframework.layout.matching.detection.AmbiguousMovementDetector;
import eu.tsystems.mms.tic.testframework.layout.matching.detection.CorrectMatchDetector;
import eu.tsystems.mms.tic.testframework.layout.matching.detection.ElementMissingDetector;
import eu.tsystems.mms.tic.testframework.layout.matching.detection.GroupMovementDetector;
import eu.tsystems.mms.tic.testframework.layout.matching.error.LayoutFeature;
import eu.tsystems.mms.tic.testframework.layout.matching.graph.DistanceGraph;
import eu.tsystems.mms.tic.testframework.layout.matching.matchers.OpenCvTemplateMatcher;
import eu.tsystems.mms.tic.testframework.layout.matching.matchers.TemplateMatchingAlgorithm;
import eu.tsystems.mms.tic.testframework.layout.reporting.GraphicalReporter;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Prerequisites are 3 images: 1. The base image that is visually approved by the Tester. 2. An annotated image where
 * elements are marked with 1 pixel wide rectangle of the color of the pixel in the top left corner (MS Paint can be a
 * reference here). 3. A image that is going to be evaluated for possible layout errors.
 * <p>
 * The basic structure is as follows: 1. Extract all annotated layout elements. If this takes very long, you probably
 * did not set the top left pixel to a marking color - that is a color that does not exist much in the image. If
 * elements are not recognized, your rectangle is probably not 1 pixel wide or incomplete. The extracted images are
 * called templates. 2. Match all templates in the actual screenshot. For each template there might be zero to many
 * matches. These are all noted in a distance graph. Additionally, the inter-template, inter-match distances are saved.
 * This should suffice to extract all interesting errors later. 3. Extract error from the distance graph. Before this is
 * done, a number of error detectors can be added, each one responsible of recognizing a different error. The found
 * errors are stored in an extra class called layoutmatch. Potentially, not only errors can be detected, but any kind of
 * feature you want. 4. The graphical reporter creates an image annotated by layout error information. 5. ??? 6. Profit.
 * <p>
 * <p>
 * User: rnhb Date: 15.05.14
 */
public class LayoutComparator {

    private LayoutMatch layoutMatch = null;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutComparator.class);

    /**
     * Instance responsible for reading the annotations.
     */
    private AnnotationReader annotationReader;

    /**
     * Instance responsible for matching the templates.
     */
    private TemplateMatcher templateMatcher;

    /**
     * Instance responsible for detecting errors in the layout by inspecting the distance graph.
     */
    private DistanceGraphInterpreter distanceGraphInterpreter;

    /**
     * Instance responsible for producing an image with visual informations about the found errors.
     */
    private GraphicalReporter graphicalReporter;

    /**
     * Minimal difference in size of the reference and actual image, to consider the reference image as sub image.
     */
    private int minimalSizeDifferenceOfSubImages;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /**
     * Public constructor
     */
    public LayoutComparator() {
        String matcherProperty = PropertyManager.getProperty(TesterraProperties.LAYOUTCHECK_MATCHING_ALGORITHM, "opencvtemplatematcher");
        TemplateMatchingAlgorithm templateMatchingAlgorithm;
        if (matcherProperty.equals("opencvtemplatematcher")) {
            templateMatchingAlgorithm = new OpenCvTemplateMatcher(OpenCvTemplateMatcher.MatchingMode.CCOEFF_NORMED);
        } else {
            throw new RuntimeException("Template Matching Algorithm \"" + matcherProperty + "\" not known, can't match with it.");
        }

        templateMatcher = new GraphBasedTemplateMatcher(templateMatchingAlgorithm);
        annotationReader = new AnnotationReader();
        graphicalReporter = new GraphicalReporter();
        distanceGraphInterpreter = new DistanceGraphInterpreter();
        distanceGraphInterpreter.addErrorDetector(new AmbiguousMovementDetector());
        distanceGraphInterpreter.addErrorDetector(new AmbiguousMatchDetector());
        distanceGraphInterpreter.addErrorDetector(new ElementMissingDetector());
        distanceGraphInterpreter.addErrorDetector(new GroupMovementDetector());
        distanceGraphInterpreter.addErrorDetector(new CorrectMatchDetector());
    }

    /**
     * This will compare the layout.
     *
     * @param referenceAbsoluteFileName           Reference image without annotations.
     * @param annotatedScreenshotAbsoluteFileName Reference image with annotations.
     * @param actualScreenshotAbsoluteFileName    Screenshot to compare.
     * @param distanceAbsoluteFileName            name of the output file
     * @param annotationDataFileName              Name of the data file containing annotations.
     * @throws FileNotFoundException if file does not exist
     */
    public void compareImages(
        String referenceAbsoluteFileName,
        String annotatedScreenshotAbsoluteFileName,
        String actualScreenshotAbsoluteFileName,
        String distanceAbsoluteFileName,
        String annotationDataFileName
    )
        throws FileNotFoundException {

        loadProperties();

        Mat referenceImage = loadImageFromFile(referenceAbsoluteFileName);
        Mat annotatedImage = loadImageFromFile(annotatedScreenshotAbsoluteFileName);
        Mat actualImage = loadImageFromFile(actualScreenshotAbsoluteFileName);
        AnnotationContainer annotationContainer = loadContainerFromFile(annotationDataFileName);

        if (annotationContainer == null) {
            // no annotator data file given, we need to rely on the image.
            // The reference image and the annotated image have to be the same size.
            if (referenceImage.height() != annotatedImage.height()
                    || referenceImage.width() != annotatedImage.width()) {
                throw new TesterraSystemException(
                        Messages.referenceAndAnnotationNotEquallySized(referenceImage.size().toString(),
                                annotatedImage.size().toString()));
            }
        }

        if (referenceImage.height() > actualImage.height() || referenceImage.width() > actualImage.width()) {
            NonFunctionalAssert.fail(
                String.format(
                    "The actual image (width=%fpx, height=%fpx) is smaller than the reference image (width=%fpx, height=%fpx)." +
                    "This should not happen, as it is ignored by the algorithm and will " +
                    "probably lead to falsely positive movement errors.",
                    actualImage.size().width,
                    actualImage.size().height,
                    referenceImage.size().width,
                    referenceImage.size().height
                )
            );
        }

        // Adjustments have to be made, if the reference image is only a part of the original screenshot.
        boolean referenceImageIsSubImage = referenceImage.height() <= actualImage.height()
                - minimalSizeDifferenceOfSubImages ||
                referenceImage.width() <= actualImage.width() - minimalSizeDifferenceOfSubImages;
        templateMatcher.setReferenceImageIsSubImage(referenceImageIsSubImage);

        // extract annotated elements
        List<LayoutElement> annotatedElements = annotationReader.extractAnnotatedElementsFromAnnotationContainer(
            referenceImage,
            annotationContainer
        );

        // create distance graph
        LOGGER.info("Comparing Reference and Actual image based on given annotations.");
        DistanceGraph distanceGraph = templateMatcher.matchTemplates(actualImage, annotatedElements);

        // interpret the distance graph
        layoutMatch = distanceGraphInterpreter.generateLayoutErrors(distanceGraph);

        Mat reportImage = prepareImageForReport(actualImage, annotatedImage);

        graphicalReporter.report(layoutMatch, reportImage, distanceAbsoluteFileName);

        /* Garbage collector has to be kind of forced here.
            openCV uses native c++, thus allocating memory not governed by java. With the java wrapper, there are small
            objects that link to the c++ objects that allocate lots of memory. When the java object is garbage collected,
            the memory allocated by c++ is also freed. However, the java objects are so small that they do not trigger
            garbage collection most of the time. Thus, the system runs out of memory.
         */
        System.gc();
        System.runFinalization();
    }

    /**
     * @param annotationDataFileName
     * @return
     */
    private AnnotationContainer loadContainerFromFile(String annotationDataFileName) {
        if (annotationDataFileName == null) {
            return null;
        }
        File file = new File(annotationDataFileName);
        if (!file.exists() || !file.isFile()) {
            LOGGER.warn("No Annotation Data file given.");
            return null;
        }
        AnnotationContainer annotationContainer;
        try {

            annotationContainer = AnnotationContainer.readFromJson(file);
        } catch (Exception e) {
            LOGGER.error("Error deserializing the data file.", e);
            return null;
        }
        return annotationContainer;
    }

    /**
     * If the annotated image is larger than the actual image, the reported distance image is too small. It is advised
     * to avoid this, but in any case we get a proper distance image this way.
     *
     * @param actualImage    actualImage
     * @param annotatedImage The annotated Image
     * @return An Image for report.
     */
    private Mat prepareImageForReport(Mat actualImage, Mat annotatedImage) {
        int actualWidth = actualImage.width();
        int annotatedWidth = annotatedImage.width();
        int actualHeight = actualImage.height();
        int annotatedHeight = annotatedImage.height();
        if (actualWidth >= annotatedWidth && actualHeight >= annotatedHeight) {
            return actualImage;
        } else {
            int maxY = Math.max(actualWidth, annotatedWidth);
            int maxX = Math.max(actualHeight, annotatedHeight);
            Mat reportMat = ImageUtil.createMat(maxX, maxY);
            for (int x = 0; x < maxX; x++) {
                for (int y = 0; y < maxY; y++) {
                    double[] doubles = actualImage.get(x, y);
                    if (doubles != null) {
                        reportMat.put(x, y, doubles);
                    }
                }
            }
            return reportMat;
        }
    }

    private void loadProperties() {
        minimalSizeDifferenceOfSubImages = PropertyManager.getIntProperty(
                TesterraProperties.LAYOUTCHECK_INTERNAL_PARAMETER_2,
                DefaultParameter.LAYOUTCHECK_INTERNAL_PARAMETER_2);
    }

    private static Mat loadImageFromFile(final String absoluteFileName) throws FileNotFoundException {
        final File file = new File(absoluteFileName);
        new Timer(100, 5000).executeSequence(new Timer.Sequence<Object>() {
            @Override
            public void run() {
                setPassState(file.exists());
                setSkipThrowingException(true);
            }
        });
        if (!file.exists()) {
            LOGGER.error("Error, File " + absoluteFileName + " not found.");
            throw new FileNotFoundException(absoluteFileName);
        }
        Mat image = ImageUtil.loadImage(file.getAbsolutePath());
        return image;
    }

    /**
     * Returns all matches that are considered as errors under the settings of the last run.
     *
     * @return List of LayoutErrors
     */
    public List<LayoutFeature> getCriticalMatches() {
        if (layoutMatch == null) {
            throw new TesterraSystemException("Layout match calculation error");
        }
        return layoutMatch.getCriticalMatches();
    }

    /**
     * Returns the relation of wrong to correct matches, in percent.
     *
     * @return Percent of wrong elements
     */
    public double getErrorRelation() {
        double size = getCriticalMatches().size();
        List<LayoutFeature> matches = layoutMatch.getCorrectMatches();
        List<LayoutFeature> ignoredCriticalMatches = layoutMatch.getIgnoredCriticalMatches();
        double matchCount = matches.size() + ignoredCriticalMatches.size() + size;
        if (matchCount > 0) {
            return Math.round(1000d * size / matchCount) / 10d;
        } else {
            return 0;
        }
    }

    public static class Messages {
        public static String tooManyMarkedPixels(String markedPercentage) {
            return "More than " + markedPercentage +
                    "% of all pixels are marked. The marking color is probably not set or poorly chosen.";
        }

        public static String tooFewMarkedPixels() {
            return "It seems that there are no annotations or that the marking color is not set correctly.";
        }

        public static String referenceAndAnnotationNotEquallySized(String referenceSize, String annotatedSize) {
            return "Reference Image (" + referenceSize + ") is not the same size as the " +
                    "annotated image (" + annotatedSize + ").";
        }
    }
    public LayoutMatch getLayoutMatch() {
        return this.layoutMatch;
    }
}
