/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */
 package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import java.io.File;
import java.util.Date;
import java.util.Optional;

public class Screenshot extends Attachment implements Loggable {

    public static class MetaData {
        public static final String SESSION_KEY="SessionKey";
        public static final String SESSION_CONTEXT_ID = "sessionContextId";
        public static final String REMOTE_SESSION_ID = "remoteSessionId";
        public static final String TITLE="Title";
        public static final String WINDOW="Window";
        public static final String URL="URL";
        public static final String DATE="Date";
        public static final String DRIVER_FOCUS="Driver Focus";
        public static final String FILE_NAME="FileName";
        public static final String SOURCE_FILE_NAME="SourceFileName";
    }
    private File pageSourceFile;

    public Screenshot() {
        this("Screenshot");
    }

    public Screenshot(String name) {
        super(name);
    }

    public Screenshot(File screenshotFile) {
        super(screenshotFile);
    }

    public Screenshot(File screenshotFile, File pageSourceFile) {
        super(screenshotFile);
        setPageSourceFile(pageSourceFile);
    }

    @Override
    public void setFile(File file) {
        getMetaData().put(MetaData.DATE, new Date(file.lastModified()).toString());
        getMetaData().put(MetaData.FILE_NAME, file.getName());
        super.setFile(file);
    }

    public File getScreenshotFile() {
        if (this.file == null) {
            this.file = createTempFile(".png");
        }
        return this.file;
    }

    public Screenshot setPageSourceFile(File file) {
        pageSourceFile = file;
        getMetaData().put(MetaData.SOURCE_FILE_NAME, file.getName());
        return this;
    }

    public Optional<File> getPageSourceFile() {
        return Optional.ofNullable(pageSourceFile);
    }

    public File createPageSourceFile() {
        return this.pageSourceFile = this.createTempFile(".html");
    }
}
