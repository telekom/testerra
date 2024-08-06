package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;

public class FailsValidator implements PropertyManagerProvider {
    public boolean expectedExceptionInvalid(MethodContext methodContext) {
        return  "haha".equals(PROPERTY_MANAGER.getProperty("test"));
    }

    public boolean expectedExceptionValid(MethodContext methodContext) {
        return  "huhu".equals(PROPERTY_MANAGER.getProperty("test"));
    }

    public boolean expectedExceptionValidForUnknownProperty(MethodContext methodContext) {
        return  "haha".equals(PROPERTY_MANAGER.getProperty("unknown.property"));
    }

    public boolean expectedExceptionValidForFailsAnnotationValid(MethodContext methodContext) {
        return  "one".equals(PROPERTY_MANAGER.getProperty("test.foobar.fails.annotation.test.property.one"));
    }

    public boolean expectedExceptionValidForFailsAnnotationNotValid(MethodContext methodContext) {
        return  "two".equals(PROPERTY_MANAGER.getProperty("test.foobar.fails.annotation.test.property.one"));
    }
}
