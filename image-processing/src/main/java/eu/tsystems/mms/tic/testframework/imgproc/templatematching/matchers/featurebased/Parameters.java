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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.imgproc.templatematching.matchers.featurebased;

import org.opencv.calib3d.Calib3d;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

/**
 * SIFT & SURF are patented! use with care! do NOT deploy to customers, only use as platform service
 */
public abstract class Parameters {
    final int UPSCALE_METHOD;
    final int UPSCALE_FACTOR;
    final int BLUR_SIZE;
    final int RANSAC_REPROJECTION_THRESHOLD;
    final float DESCRIPTOR_DISTANCE_LOWE;
    final int HOMOGRAPHY_METHOD; // Calib3d.RANSAC or Calib3d.LSMED
    final int DETECTOR;
    final int EXTRACTOR;
    final int MATCHER;
    int matchProjectionInnerAngleDegreeThreshold = 20;

    public Parameters(int upscaleMethod, int upscaleFactor, int blurSize, int ransacReprojectionThreshold, float descriptorDistanceLowe, int homographyMethod, int detector, int extractor, int matcher) {
        this.UPSCALE_METHOD = upscaleMethod;
        this.UPSCALE_FACTOR = upscaleFactor;
        this.BLUR_SIZE = blurSize;
        this.RANSAC_REPROJECTION_THRESHOLD = ransacReprojectionThreshold;
        this.DESCRIPTOR_DISTANCE_LOWE = descriptorDistanceLowe;
        this.HOMOGRAPHY_METHOD = homographyMethod;
        this.DETECTOR = detector;
        this.EXTRACTOR = extractor;
        this.MATCHER = matcher;
    }

    public int getMatchProjectionInnerAngleDegreeThreshold() {
        return matchProjectionInnerAngleDegreeThreshold;
    }

    public void setMatchProjectionInnerAngleDegreeThreshold(int matchProjectionInnerAngleDegreeThreshold) {
        this.matchProjectionInnerAngleDegreeThreshold = matchProjectionInnerAngleDegreeThreshold;
    }

    public abstract String toString();

    public static Parameters siftSiftBf() {
        return new Parameters(
                Imgproc.INTER_LANCZOS4,
                1,
                4,
                10,
                0.7F,
                Calib3d.RANSAC,
                FeatureDetector.SIFT,
                DescriptorExtractor.OPPONENT_SIFT,
                DescriptorMatcher.BRUTEFORCE
        ) {
            @Override
            public String toString() {
                return "siftSiftBf";
            }
        };
    }

    public static Parameters pyramidSiftSiftBf() {
        return new Parameters(
                Imgproc.INTER_LANCZOS4,
                1,
                3,
                10,
                0.7F,
                Calib3d.RANSAC,
                FeatureDetector.PYRAMID_SIFT,
                DescriptorExtractor.OPPONENT_SIFT,
                DescriptorMatcher.BRUTEFORCE
        ) {
            @Override
            public String toString() {
                return "pyramidSiftSiftBf";
            }
        };
    }

    public static Parameters pyramidSiftSiftFlann() {
        return new Parameters(
                Imgproc.INTER_LANCZOS4,
                1,
                4,
                10,
                0.7F,
                Calib3d.RANSAC,
                FeatureDetector.PYRAMID_SIFT,
                DescriptorExtractor.OPPONENT_SIFT,
                DescriptorMatcher.BRUTEFORCE
        ) {
            @Override
            public String toString() {
                return "pyramidSiftSiftFlann";
            }
        };
    }

    public static Parameters pyramidFastSurfFlann() {
        return new Parameters(
                Imgproc.INTER_LANCZOS4,
                1,
                4,
                10,
                0.7F,
                Calib3d.RANSAC,
                FeatureDetector.PYRAMID_FAST,
                DescriptorExtractor.OPPONENT_SURF,
                DescriptorMatcher.FLANNBASED
        ) {
            @Override
            public String toString() {
                return "pyramidFastSurfFlann";
            }
        };
    }

    public static Parameters pyramidSurfOppSurfFlann() {
        return new Parameters(
                Imgproc.INTER_LANCZOS4,
                1,
                4,
                10,
                0.7F,
                Calib3d.RANSAC,
                FeatureDetector.PYRAMID_SURF,
                DescriptorExtractor.OPPONENT_SURF,
                DescriptorMatcher.FLANNBASED
        ) {
            @Override
            public String toString() {
                return "pyramidSurfOppSurfFlann";
            }
        };
    }

    public static Parameters pyramidSiftBriskBf() {
        return new Parameters(
                Imgproc.INTER_LANCZOS4,
                1,
                4,
                10,
                0.7F,
                Calib3d.RANSAC,
                FeatureDetector.PYRAMID_SIFT,
                DescriptorExtractor.OPPONENT_BRISK,
                DescriptorMatcher.BRUTEFORCE
        ) {
            @Override
            public String toString() {
                return "pyramidSiftBriskBf";
            }
        };
    }

    public static Parameters pyramidSiftOppOrbBf() {
        return new Parameters(
                Imgproc.INTER_LANCZOS4,
                1,
                4,
                10,
                0.7F,
                Calib3d.RANSAC,
                FeatureDetector.PYRAMID_SIFT,
                DescriptorExtractor.OPPONENT_ORB,
                DescriptorMatcher.BRUTEFORCE) {
            @Override
            public String toString() {
                return "pyramidSiftOppOrbBf";
            }
        };
    }

    //    Params(int UPSCALE_METHOD, int UPSCALE_FACTOR, int BLUR_SIZE, int RANSAC_REPROJECTION_THRESHOLD, float DESCRIPTOR_DISTANCE_LOWE, int HOMOGRAPHY_METHOD, int DETECTOR, int EXTRACTOR, int MATCHER) {
//        this.UPSCALE_METHOD = UPSCALE_METHOD;
//        this.UPSCALE_FACTOR = UPSCALE_FACTOR;
//        this.BLUR_SIZE = BLUR_SIZE;
//        this.RANSAC_REPROJECTION_THRESHOLD = RANSAC_REPROJECTION_THRESHOLD;
//        this.DESCRIPTOR_DISTANCE_LOWE = DESCRIPTOR_DISTANCE_LOWE;
//        this.HOMOGRAPHY_METHOD = HOMOGRAPHY_METHOD;
//        this.DETECTOR = DETECTOR;
//        this.EXTRACTOR = EXTRACTOR;
//        this.MATCHER = MATCHER;
//    }
    public static Parameters getDefault() {
        return Parameters.pyramidSiftSiftBf();
    }
}
