package eu.tsystems.mms.tic.testframework.common;

/**
 * New Properties with overridable default value
 * @author Mike Reiche
 */
public interface IProperties {
    /**
     * Sets a new default value
     * @param defaultValue
     * @return this
     */
    IProperties newDefault(Object defaultValue);
    /**
     * @return Value as double
     */
    Double asDouble();
    /**
     * @return Value as long
     */
    Long asLong();
    /**
     * @return Value as boolean
     */
    Boolean asBool();
    /**
     * @return Value as string
     */
    String asString();
    /**
     * @return Property string
     */
    String toString();
}
