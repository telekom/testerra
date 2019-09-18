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

import java.util.*;

public class Screenshot {

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
    public String sourceFilename;
    final private Map<String, String> meta = new HashMap<>();

    /*
    Refers to the errorContext the screenshot belongs to.
     */
    public String errorContextId;

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
