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
package eu.tsystems.mms.tic.testframework.imgproc;

import eu.tsystems.mms.tic.testframework.imgproc.templatematching.matchers.CheckedCvException;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class ImgprocUtils {
    public static final DateTimeFormatter FORMATTER_FILENAME_FRIENDLY_WITH_MILLISECONDS = DateTimeFormatter.ofPattern("uuuu-MM-dd_HH-mm-ss.SSS");

    public static ZonedDateTime now() {
        return ZonedDateTime.now();
    }

    public static String timestamp() {
        return FORMATTER_FILENAME_FRIENDLY_WITH_MILLISECONDS.format(ZonedDateTime.now());
    }

    public static synchronized Mat loadImage(Path path) throws CheckedCvException {
        try {
            return Highgui.imread(path.toAbsolutePath().toString());
        } catch (CvException e) {
            throw new CheckedCvException("could not load image", e);
        }
    }

    public static synchronized void writeImage(Mat mat, Path path) throws CheckedCvException {
        try {
            Highgui.imwrite(path.toAbsolutePath().toString(), mat);
        } catch (CvException e) {
            throw new CheckedCvException("could not load image", e);
        }
    }
}
