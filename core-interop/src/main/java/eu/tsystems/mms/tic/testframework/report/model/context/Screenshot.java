/*
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
package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Screenshot extends Attachment implements Loggable {

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

    @Deprecated
    public String sourceFilename;
    private File pageSourceFile;
    final private Map<String, String> meta = new HashMap<>();

    public Screenshot() {
        this("Screenshot");
    }

    public Screenshot(String name) {
        super(name);
    }

    public Screenshot(File screenshotFile, File pageSourceFile) {
        super(screenshotFile);
        if (pageSourceFile!=null) {
            setPageSourceFile(pageSourceFile);
        }
    }

    @Override
    protected Attachment setFile(File file) {
        meta.put(Meta.DATE.toString(), new Date(file.lastModified()).toString());
        meta.put(Meta.FILE_NAME.toString(), file.getName());
        return super.setFile(file);
    }

    public File getScreenshotFile() {
        return getOrCreateTempFile(".png");
    }

    private Screenshot setPageSourceFile(File file) {
        sourceFilename = file.getName();
        pageSourceFile = file;
        meta.put(Meta.SOURCE_FILE_NAME.toString(), sourceFilename);
        return this;
    }

    public File getPageSourceFile() {
        if (pageSourceFile==null) {
            try {
                File file = File.createTempFile(FilenameUtils.getBaseName(getScreenshotFile().getName()), ".html");
                if (file.exists()) file.delete();
                setPageSourceFile(file);
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
