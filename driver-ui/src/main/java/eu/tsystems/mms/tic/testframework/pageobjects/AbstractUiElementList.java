package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicUiElement;

import java.util.Iterator;

public abstract class AbstractUiElementList<SELF extends BasicUiElement> implements UiElementList<SELF> {
    private final SELF guiElement;
    private int iteratorIndex = 0;
    private int iteratorSize = 0;

    public AbstractUiElementList(SELF guiElement) {
        this.guiElement = guiElement;
    }

    @Override
    public int size() {
        return guiElement.numberOfElements().getActual();
    }

    @Override
    public boolean isEmpty() {
        return size()==0;
    }
    @Override
    public Iterator<SELF> iterator() {
        iteratorIndex = 0;
        iteratorSize = size();
        return this;
    }

    @Override
    abstract public SELF get(int i);

    @Override
    public boolean hasNext() {
        return iteratorIndex < iteratorSize;
    }

    @Override
    public SELF next() {
        return get(iteratorIndex++);
    }
}
