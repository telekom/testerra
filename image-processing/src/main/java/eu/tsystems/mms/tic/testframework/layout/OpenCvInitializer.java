package eu.tsystems.mms.tic.testframework.layout;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by rnhb on 19.06.2015.
 */
public class OpenCvInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenCvInitializer.class);
    private static boolean initialized = false;

    public static void init() {
        // do not initialize again if already done
        if (initialized) {
            return;
        }

        // find platform
        String osname = System.getProperty("os.name");
        String arch = System.getProperty("os.arch");

        String libraryFileName = "opencv_java249_32.dll"; // default: windows 32bit

        if (osname != null) {
            osname = osname.toLowerCase();
            if (osname.contains("inux")) {
                if (arch != null && arch.contains("64")) {
                    libraryFileName = "libopencv_java249_64.so";
                } else {
                    libraryFileName = "libopencv_java249_32.so";
                }
            } else if (osname.contains("win")) {
                if (arch != null && arch.contains("64")) {
                    libraryFileName = "opencv_java249_64.dll";
                }
                // else use default
            } else {
                throw new TesterraSystemException("Unsupported os: " + osname);
            }
        }

        // extract lib
        String resourceFileName = "opencv/" + libraryFileName;
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceFileName);
        if (is == null) {
            LOGGER.error("Could not load resource: " + resourceFileName);
            throw new TesterraSystemException("Could not load resource: " + resourceFileName);
        }

        int readBytes;
        byte[] buffer = new byte[1024];
        try {
            File libCopy = new File(libraryFileName);
            libCopy.deleteOnExit();
            OutputStream os = new FileOutputStream(libCopy);
            try {
                while ((readBytes = is.read(buffer)) != -1) {
                    os.write(buffer, 0, readBytes);
                }
            } finally {
                // If read/write fails, close streams safely before throwing an exception
                os.close();
                is.close();
            }

            // load lib
            System.load(libCopy.getAbsolutePath());
            // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (IOException e) {
            LOGGER.error("Error preparing " + libraryFileName);
            throw new TesterraSystemException("Error preparing " + libraryFileName, e);
        }

        LOGGER.info("Successfully loaded " + resourceFileName);

        initialized = true;
    }
}
