package eu.tsystems.mms.tic.testframework.common;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;

public class DefaultPropertyManager implements IPropertyManager, MethodEndEvent.Listener {
    @Override
    public void setTestLocalProperty(String key, Object value) {
        if (value == null) {
            removeTestLocalProperty(key);
        } else {
            PropertyManager.getThreadLocalProperties().setProperty(key, value.toString());
        }
    }

    @Override
    public void setSystemProperty(String key, Object value) {
        if (value == null) {
            removeSystemProperty(key);
        } else {
            System.getProperties().setProperty(key, value.toString());
        }
    }

    @Override
    public void removeSystemProperty(String key) {
        System.getProperties().remove(key);
    }

    @Override
    public void removeTestLocalProperty(String key) {
        PropertyManager.getThreadLocalProperties().remove(key);
    }

    @Override
    public boolean loadProperties(String resourceFile) {
        return PropertyManager.pLoadPropertiesFromResource(PropertyManager.FILEPROPERTIES, resourceFile, null);
    }

    public String getProperty(String key, Object defaultValue) {
        return PropertyManager.getPropertiesParser().getProperty(key, defaultValue);
    }

    public double getDoubleProperty(String key, Object defaultValue) {
        return PropertyManager.getPropertiesParser().getDoubleProperty(key, defaultValue);
    }

    public long getLongProperty(String key, Object defaultValue) {
        return PropertyManager.getPropertiesParser().getLongProperty(key, defaultValue);
    }
    public boolean getBooleanProperty(final String key, Object defaultValue) {
        return PropertyManager.getPropertiesParser().getBooleanProperty(key, defaultValue);
    }

    @Override
    @Subscribe
    public void onMethodEnd(MethodEndEvent event) {
        if (event.getTestMethod().isTest()) {
            PropertyManager.clearThreadlocalProperties();
        }
    }
}
