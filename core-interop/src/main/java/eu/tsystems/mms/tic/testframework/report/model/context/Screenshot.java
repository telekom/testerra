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
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.logging.Loggable;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Screenshot implements Loggable {

    public enum Meta {
        SESSION_KEY("SessionKey"),
        TITLE("Title"),
        WINDOW("Window"),
        URL("URL"),
        DATE("Date"),
        DRIVER_FOCUS("Driver Focus"),
        FILE_NAME("FileName"),
        SOURCE_FILE_NAME("SourceFileName"),
        ;

        private String key;

        Meta(final String key) {
            this.key = key;
        }

        public String toString() {
            return key;
        }
    }

    public String filename;
    private File screenshotFile;
    public String sourceFilename;
    private File pageSourceFile;
    final private Map<String, String> meta = new HashMap<>();
    private static HashMap<String, Integer> counter = new HashMap<>();
    private final String name;

    /*
    Refers to the errorContext the screenshot belongs to.
     */
    public String errorContextId;

    public Screenshot() {
        this("Screenshot");
    }

    public Screenshot(String name) {
        meta.put(Meta.DATE.toString(), new Date().toString());
        int count = counter.getOrDefault(name, 1);
        this.name = String.format("%s-%03d-", name, count);
        counter.put(name, ++count);
    }

    public Screenshot setScreenshotFile(File screenshotFile) {
        this.screenshotFile = screenshotFile;
        return this;
    }

    public File getScreenshotFile() {
        if (screenshotFile==null) {
            try {
                screenshotFile = File.createTempFile(name, ".png");
                if (screenshotFile.exists()) screenshotFile.delete();
                filename = screenshotFile.getName();
                meta.put(Meta.FILE_NAME.toString(), filename);
            } catch (IOException e) {
                log().error(e.getMessage());
            }
        }
        return screenshotFile;
    }

    public File getPageSourceFile() {
        if (pageSourceFile==null) {
            try {
                pageSourceFile = File.createTempFile(name, ".html");
                if (pageSourceFile.exists()) pageSourceFile.delete();
                sourceFilename = pageSourceFile.getName();
                meta.put(Meta.SOURCE_FILE_NAME.toString(), sourceFilename);
            } catch (IOException e) {
                log().error(e.getMessage());
            }
        }
        return pageSourceFile;
    }

    @Override
    public String toString() {
        return "Screenshot{" +
                "filename='" + filename + '\'' +
                ", sourceFilename='" + sourceFilename + '\'' +
                ", meta=" + meta +
                '}';
    }

    public Map<String, String> meta() {
        return meta;
    }
}
