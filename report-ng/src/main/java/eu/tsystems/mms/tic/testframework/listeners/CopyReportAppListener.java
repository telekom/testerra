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
import java.net.URL;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.ProviderNotFoundException;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
            for (Path resourcePath : getPathsFromResource("report-ng").collect(Collectors.toList())) {
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
     * This methods tries to read all files from a resource directory.
     * When this methods runs inside a JAR, it reads the directory listing from the JAR's resource folder:
     *      application.jar#/folder/...
     * and maps them to relative resource paths:
     *      /folder/file1.jpg
     *      /folder/file2.js
     *      ...
     * Otherwise, it reads the directory listing from the local resource folder:
     *      ../build/resources/main/folder/...
     * and maps the files to relative resource paths:
     *       /folder/file1.jpg
     *       /folder/file2.js
     *       ...
     *
     * In both cases, you need to read the actual resource by {@link ClassLoader#getResource(String)}.
     * @see {https://mkyong.com/java/java-read-a-file-from-resources-folder/}
     */
    private Stream<Path> getPathsFromResource(String folder) throws URISyntaxException, IOException {
        // get path of the current running JAR
        URI pathUri = getClass()
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI();

        try {
            URI uri = URI.create("jar:file:" + pathUri.getRawPath());
            FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
            Path resourceDirPath = fs.getPath(folder);
            return Files.walk(resourceDirPath).filter(Files::isRegularFile);
        } catch (IOException | ProviderNotFoundException e) {
            URL resourceUrl = getClass().getClassLoader().getResource(folder);
            log().warn(String.format("Unable to read from resource JAR: %s, trying local resources: %s", e.getMessage(), resourceUrl));
            Path resourceDirPath = Paths.get(resourceUrl.toURI());
            return Files.walk(resourceDirPath)
                    .filter(Files::isRegularFile)
                    .map(resourceFilePath -> resourceDirPath.getParent().relativize(resourceFilePath));
        }
    }
}
