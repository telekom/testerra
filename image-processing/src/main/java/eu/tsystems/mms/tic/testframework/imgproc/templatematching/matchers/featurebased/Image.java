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

import eu.tsystems.mms.tic.testframework.imgproc.templatematching.matchers.CheckedCvException;
import eu.tsystems.mms.tic.testframework.imgproc.templatematching.matchers.MatcherException;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static eu.tsystems.mms.tic.testframework.imgproc.templatematching.matchers.featurebased.Matcher.params;

class Image implements FreesNativeMemory {
    protected MatOfKeyPoint features = new MatOfKeyPoint();
    protected Mat descriptors = new Mat();
    protected List<KeyPoint> featuresList = new LinkedList<>();
    protected Mat image;
    protected Mat originalImage;

    protected DescriptorExtractor extractor = DescriptorExtractor.create(params().EXTRACTOR);
    protected FeatureDetector detector = FeatureDetector.create(params().DETECTOR);

    protected static final Logger LOGGER = LoggerFactory.getLogger(Image.class);

    Image(Path path) throws Exception {
        String pathStr = path.toAbsolutePath().toString();
        if (!path.toFile().exists()) {
            throw new IOException("could not load image file, aborting. Path : " + path);
        }
        Mat mat = Highgui.imread(pathStr);
        if (mat.empty()) {
            throw new Exception("loaded image was empty, aborting. Path : " + path);
        }
        init(mat);
    }

    Image(Mat image) throws Exception {
        if (image == null || image.empty()) {
            throw new Exception("loaded image was empty, aborting");
        }
        init(image);
    }

    private void init(Mat image) {
        this.image = image;
        originalImage = image;
    }

    void scale(int factor) {
        if (params().UPSCALE_FACTOR != 1) {
            image = resize(image);
        }
    }

    Point scaleDown(Point scaled) {
        if (params().UPSCALE_FACTOR != 1) {
            return new Point((int) scaled.x / params().UPSCALE_FACTOR, (int) scaled.y / params().UPSCALE_FACTOR);
        } else {
            return new Point(scaled.x, scaled.y);
        }
    }

    protected Mat resize(Mat image) {
        Mat ret = new Mat();
        Imgproc.resize(
                image,
                ret,
                new Size(image.width() * params().UPSCALE_FACTOR, image.height() * params().UPSCALE_FACTOR),
                0,
                0,
                params().UPSCALE_METHOD);
        return ret;
    }

    synchronized void detectFeatures() throws CheckedCvException, MatcherException {
        try {
            if (features != null)
                features.release();
            features = new MatOfKeyPoint();
            if (image.empty()) {
                throw new MatcherException("image not loaded, cant detect features on empty image");
            }
            detector.detect(image, features);
            featuresList = features.toList();
        } catch (CvException e) {
            throw new CheckedCvException(e);
        }
    }

    synchronized void computeDescriptors() throws CheckedCvException, MatcherException {
        try {
            if (descriptors != null)
                descriptors.release();
            descriptors = new Mat();
            if (features.empty()) {
                throw new MatcherException("no features detected yet, cant compute descriptors");
            }
            extractor.compute(image, features, descriptors);
        } catch (CvException e) {
            throw new CheckedCvException(e);
        }
    }

    ////////////
    // GETTERS
    ////////////
    Mat getImage() {
        return image;
    }

    Mat getDescriptors() {
        return descriptors;
    }

    MatOfKeyPoint getFeatures() {
        return features;
    }

    List<KeyPoint> getFeaturesList() {
        return featuresList;
    }

    Mat getOriginalImage() {
        return originalImage;
    }

    void blur(double size) throws CheckedCvException {
        try {
            size = (size == 0) ? 1 : size;
            Size kernelSize = new Size(size, size);
            Mat blurred = new Mat();
            Imgproc.blur(image, blurred, kernelSize);
            if (image != null)
                image.release();
            image = blurred;
        } catch (CvException e) {
            throw new CheckedCvException(e);
        }
    }

    @Override
    public void release() {
        image.release();
        originalImage.release();
        descriptors.release();
        features.release();
    }
}
