package eu.tsystems.mms.tic.testframework.common;

public interface IProperties {
    IProperties useDefault(Object defaultValue);
    Double asDouble();
    Long asLong();
    Integer asInt();
    Boolean asBool();
}
