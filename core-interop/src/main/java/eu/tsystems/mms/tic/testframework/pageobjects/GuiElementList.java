package eu.tsystems.mms.tic.testframework.pageobjects;

import java.util.Iterator;

public interface GuiElementList<SELF> extends
    Iterable<SELF>,
    Iterator<SELF>
{
    SELF get(int i);
    int size();

    default boolean isEmpty() {
        return size()==0;
    }

    default SELF first() {
        return get(0);
    }

    default SELF last() {
        return get(size()-1);
    }
}
