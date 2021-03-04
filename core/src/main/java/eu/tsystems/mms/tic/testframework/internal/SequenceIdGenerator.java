package eu.tsystems.mms.tic.testframework.internal;

public class SequenceIdGenerator implements IdGenerator {

    private static long id = 0;

    @Override
    public Object generate() {
        return ++id;
    }
}
