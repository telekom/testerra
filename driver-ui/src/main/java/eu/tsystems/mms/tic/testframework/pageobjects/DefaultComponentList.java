package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;

public class DefaultComponentList<COMPONENT extends AbstractComponent<COMPONENT>> extends AbstractUiElementList<COMPONENT> {
    private final PageObjectFactory pageFactory = Testerra.injector.getInstance(PageObjectFactory.class);
    private COMPONENT component;

    public DefaultComponentList(COMPONENT component) {
        super(component);
        this.component = component;
    }

    @Override
    public COMPONENT get(int i) {
        COMPONENT component = (COMPONENT)pageFactory.createComponent(this.component.getClass(), this.component.rootElement.list().get(i));
        component.setParent(this.component.getParent());
        return component;
    }
}
