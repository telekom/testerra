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
package eu.tsystems.mms.tic.testframework.exceptions;

/**
 * Created by pele on 24.11.2014.
 */
public class FileNotFoundException extends Exception {

    private static final String ERROR_TEXT = "File not found: ";

    private FileNotFoundException() {
    }

    public FileNotFoundException(String filename) {
        super(ERROR_TEXT + filename);
    }

    public FileNotFoundException(String filename, Throwable cause) {
        super(ERROR_TEXT + filename, cause);
    }

    private FileNotFoundException(Throwable cause) {
        super(cause);
    }

    private FileNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
