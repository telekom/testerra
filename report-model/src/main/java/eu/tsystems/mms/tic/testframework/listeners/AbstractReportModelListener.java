/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */

package eu.tsystems.mms.tic.testframework.listeners;

import com.google.protobuf.Message;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import java.io.File;
import java.io.FileOutputStream;

public abstract class AbstractReportModelListener implements Loggable {
    protected final File baseDir;
    private File classesDir;
    private File filesDir;
    private File methodsDir;
    private File testsDir;
    private File suitesDir;

    public AbstractReportModelListener(File baseDir) {
        this.baseDir = baseDir;
        baseDir.mkdirs();
    }

    protected File getBaseDir() {
        return baseDir;
    }

    protected File getClassesDir() {
        if (classesDir == null) {
            classesDir = new File(baseDir, "classes");
            classesDir.mkdir();
        }
        return classesDir;
    }

    protected File getFilesDir() {
        if (filesDir == null) {
            filesDir = new File(baseDir, "files");
            filesDir.mkdir();
        }
        return filesDir;
    }

    protected File getMethodsDir() {
        if (methodsDir == null) {
            methodsDir = new File(baseDir, "methods");
            methodsDir.mkdir();
        }
        return methodsDir;
    }

    protected File getTestsDir() {
        if (testsDir == null) {
            testsDir = new File(baseDir, "tests");
            testsDir.mkdir();
        }
        return testsDir;
    }

    protected File getSuitesDir() {
        if (suitesDir == null) {
            suitesDir = new File(baseDir, "suites");
            suitesDir.mkdir();
        }
        return suitesDir;
    }

    protected void writeBuilderToFile(Message.Builder builder, File file) {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            builder.build().writeTo(stream);
            stream.close();
        } catch (Exception e) {
            log().error("Unable to write file", e);
        }
    }
}
