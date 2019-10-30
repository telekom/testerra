package eu.tsystems.mms.tic.testframework.common;

public interface IProperties<T> {
    /**
     * Sets a new default value
     * @param defaultValue
     * @return this
     */
    IProperties useDefault(Object defaultValue);
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
