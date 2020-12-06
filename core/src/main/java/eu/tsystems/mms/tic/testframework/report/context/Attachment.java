/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.report.context;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Attachment implements Loggable {
    private static HashMap<String, Integer> counter = new HashMap<>();
    private String tmpName;
    private File file;
    private String errorContextId;
    private Map<String, String> meta;

    /**
     * @todo We may pass name and extension later.
     */
    public Attachment(String name) {
        int count = counter.getOrDefault(name, 1);
        tmpName = String.format("%s-%03d-", name, count);
        counter.put(name, ++count);
    }

    public Attachment(File file) {
        setFile(file);
    }

    protected File getOrCreateTempFile(String withSuffix) {
        if (file == null) {
            try {
                file = File.createTempFile(tmpName, withSuffix);
                if (file.exists()) file.delete();
            } catch (IOException e) {
                log().error(e.getMessage());
            }
        }
        return file;
    }

    protected File getFile() {
        return file;
    }

    public Attachment setFile(File file) {
        this.file = file;
        return this;
    }

    @Deprecated
    public Attachment setErrorContextId(String id) {
        this.errorContextId = id;
        return this;
    }

    @Deprecated
    public String getErrorContextId() {
        return this.errorContextId;
    }

    @Deprecated
    public boolean hasErrorContextId() {
        return this.errorContextId != null;
    }

    public Map<String, String> meta() {
        if (meta == null) {
            meta = new HashMap<>();
        }
        return meta;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "file=" + file +
                ", meta=" + meta +
                '}';
    }
}
