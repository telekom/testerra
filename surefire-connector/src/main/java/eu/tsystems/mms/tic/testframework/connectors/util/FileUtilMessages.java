package eu.tsystems.mms.tic.testframework.connectors.util;

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
