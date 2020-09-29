package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Attachment implements Loggable {
    private static HashMap<String, Integer> counter = new HashMap<>();
    private String tmpName;
    private File file;
    @Deprecated
    public String filename;
    /*
    Refers to the errorContext the screenshot belongs to.
     */
    @Deprecated
    public String errorContextId;
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
        filename = file.getName();
        this.file = file;
        return this;
    }

    public Attachment setErrorContextId(String id) {
        this.errorContextId = id;
        return this;
    }

    public String getErrorContextId() {
        return this.errorContextId;
    }

    public boolean hasErrorContext() {
        return this.errorContextId != null;
    }

    public Map<String, String> meta() {
        if (meta == null) {
            meta = new HashMap<>();
        }
        return meta;
    }
}
