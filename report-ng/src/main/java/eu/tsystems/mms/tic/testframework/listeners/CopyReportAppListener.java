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

package eu.tsystems.mms.tic.testframework.listeners;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.FinalizeExecutionEvent;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

public class CopyReportAppListener implements FinalizeExecutionEvent.Listener, Loggable {

    private File targetDir;

    public CopyReportAppListener(File targetDir) {
        this.targetDir = targetDir;
    }

    @Subscribe
    @Override
    public void onFinalizeExecution(FinalizeExecutionEvent event) {
        try {
            for (Path resourcePath : getPathsFromResourceJAR("report-ng")) {
                final String stringRepresentationOfResourcePath = StringUtils.stripStart(resourcePath.toString(), "/");
                try (final InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(stringRepresentationOfResourcePath)) {
                    if (resourceStream == null) {
                        throw new Exception("Could not open stream: " + stringRepresentationOfResourcePath);
                    }
                    final File targetFile = new File(this.targetDir, resourcePath.toString());
                    FileUtils.copyInputStreamToFile(resourceStream, targetFile);
                }
            }
        } catch (Exception e) {
            log().error("Unable to copy app resource", e);
        }
    }

    /**
     * @see {https://mkyong.com/java/java-read-a-file-from-resources-folder/}
     */
    private List<Path> getPathsFromResourceJAR(String folder) throws URISyntaxException, IOException {
        // get path of the current running JAR
        String jarPath = getClass().getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();

        List<Path> result;

        // file walks JAR
        URI uri = URI.create("jar:file:" + jarPath);
        try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
            result = Files.walk(fs.getPath(folder))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }

        return result;
    }
}
