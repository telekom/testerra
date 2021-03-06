package eu.tsystems.mms.tic.testframework.common;

public interface IPropertyManager {
    void setTestLocalProperty(String key, Object value);

    void setSystemProperty(String key, Object value);

    void removeSystemProperty(String key);

    void removeTestLocalProperty(String key);

    void loadProperties(String resourceFile);

    default String getProperty(String key) {
        return getProperty(key, null);
    }

    String getProperty(String key, Object defaultValue);

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     *
     * @return property value
     */
    double getDoubleProperty(String key, Object defaultValue);

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key key of the property
     *
     * @return property value or -1 if value cannot be parsed or is not set.
     */
    default double getDoubleProperty(String key) {
        return getDoubleProperty(key, -1);
    }

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     *
     * @return property value
     */
    long getLongProperty(String key, Object defaultValue);

    /**
     * Gets the value of the property identified by its key.
     *
     * @param key key of the property
     *
     * @return property value or -1 if value cannot be parsed or is not set.
     */
    default long getLongProperty(String key) {
        return getLongProperty(key, -1);
    }

    /**
     * Get boolean property.
     *
     * @param key true or false.
     *
     * @return boolean property value or default false, if property is not set
     *
     * @see java.lang.Boolean#parseBoolean(String)
     */
    default boolean getBooleanProperty(String key) {
        return getBooleanProperty(key, false);
    }

    /**
     * Gets the value of the property or the default identified by its key.
     *
     * @param key          key of the property
     * @param defaultValue default value
     *
     * @return property value
     */
    boolean getBooleanProperty(final String key, Object defaultValue);
}
