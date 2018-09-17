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
package eu.tsystems.mms.tic.testframework.test.constants;

/**
 * User: rnhb
 * Date: 13.12.13
 */
public final class FileUtilMessages {

    private FileUtilMessages() {
    }

    public static String entryInFile(String entry, String filePath) {
        return "Found entry \""
                + entry
                + "\" in the file at "
                + filePath
                + ".";
    }

    public static String entryNotInFile(String entry, String filePath) {
        return "Couldn't find entry \""
                + entry
                + "\" in the file at "
                + filePath
                + ".";
    }

    public static String fileNotFound(String path) {
        return "No file found at \""
                + path
                + "\".";
    }

    public static String methodNotFoundInReport(String methodName, String belongingTable) {
        return "The method \""
                + methodName
                + "\" was not found in the report under \""
                + belongingTable
                + "\".";
    }

    public static String wrongMethodCountInReport(String type) {
        return "The total number of Tests is wrong for "
                + type
                + " Tests.";
    }

    public static String stringNotInBetween(String path, String start, String inBetween, String end) {
        return "The String \""
                + inBetween
                + "\" was not found between the Strings \""
                + start
                + "\" and \""
                + end
                + "\" in the file \""
                + path
                + "\"";
    }
}
