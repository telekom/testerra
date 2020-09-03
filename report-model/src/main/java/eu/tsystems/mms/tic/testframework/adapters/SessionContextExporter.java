package eu.tsystems.mms.tic.testframework.adapters;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.SessionContext;
import eu.tsystems.mms.tic.testframework.report.model.SessionContext.Builder;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import static eu.tsystems.mms.tic.testframework.report.model.SessionContext.newBuilder;

public class SessionContextExporter extends AbstractContextExporter implements Loggable {

    public SessionContext.Builder prepareSessionContext(eu.tsystems.mms.tic.testframework.report.model.context.SessionContext sessionContext) {
        Builder builder = newBuilder();

        apply(createContextValues(sessionContext), builder::setContextValues);
        apply(sessionContext.sessionKey, builder::setSessionKey);
        apply(sessionContext.provider, builder::setProvider);
        apply(sessionContext.sessionId, builder::setSessionId);

        // translate object map to string map
        Map<String, String> newMap = new LinkedHashMap<>();
        for (String key : sessionContext.metaData.keySet()) {
            Object value = sessionContext.metaData.get(key);
            if (StringUtils.isStringEmpty(key) || value == null || StringUtils.isStringEmpty(value.toString())) {
                log().error("Not exporting sessionContext.metaData[" + key + "]=" + value);
            } else {
                newMap.put(key, value.toString());
            }
        }
        builder.putAllMetadata(newMap);

        return builder;
    }

}
