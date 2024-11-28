/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.annotations.PageOptions;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.AbstractComponent;
import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.Component;
import eu.tsystems.mms.tic.testframework.pageobjects.EmptyUiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locator;
import eu.tsystems.mms.tic.testframework.pageobjects.LocatorFactoryProvider;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.PageObject;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.XPath;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.AbstractFieldAction;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.GuiElementCheckFieldAction;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.action.SetNameFieldAction;
import eu.tsystems.mms.tic.testframework.testing.TestControllerProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * This is an abstract page object used for {@link Page} and {@link AbstractComponent}.
 * Provides basic {@link PageObject} related features:
 * Supports element {@link Check}
 * Supports {@link PageOptions}
 * Livecycle methods for {@link #checkUiElements(CheckRule)}:
 * {@link #checkPagePreparation()}
 * {@link #addCustomFieldActions}
 * <p>
 * {@link #checkPageErrorState(Throwable)}
 *
 * @author Peter Lehmann
 * @author Mike Reiche
 * @todo Rename to AbstractPageObject
 * @see {https://martinfowler.com/bliki/PageObject.html}
 */
public abstract class AbstractPage<SELF> implements
        Loggable,
        TestControllerProvider,
        PageObject<SELF>,
        LocatorFactoryProvider,
        PageCreator {
    protected static final PageFactory pageFactory = Testerra.getInjector().getInstance(PageFactory.class);

    abstract protected UiElement find(Locator locator);

    abstract protected UiElement findDeep(Locator locator);

    protected UiElement findById(Object id) {
        return find(LOCATE.by(By.id(id.toString())));
    }

    protected UiElement findByQa(String qa) {
        return find(LOCATE.byQa(qa));
    }

    protected UiElement find(By by) {
        return find(LOCATE.by(by));
    }

    protected UiElement find(XPath xPath) {
        return find(LOCATE.by(xPath));
    }

    protected UiElement findDeep(XPath xPath) {
        return findDeep(LOCATE.by(xPath));
    }

    protected UiElement findDeep(By by) {
        return findDeep(LOCATE.by(by));
    }

    protected UiElement createEmpty() {
        return createEmpty(LOCATE.by(By.tagName("empty")));
    }

    protected UiElement createEmpty(Locator locator) {
        return new EmptyUiElement(this, locator);
    }

    /**
     * Package private accessible by {@link PageFactory}
     */
    void checkUiElements() throws Throwable {
        checkUiElements(CheckRule.DEFAULT);
    }

    /**
     * Package private accessible by {@link PageFactory}
     */
    void checkUiElements(CheckRule checkRule) throws Throwable {
        /*
        page checks
         */
        checkPagePreparation();
        try {
            checkAnnotatedFields(checkRule);
        } catch (Throwable throwable) {
            // call page error state logic
            checkPageErrorState(throwable);
        }

        pageLoaded();
    }

    public abstract String getName(boolean detailed);

    /**
     * Allows pages to run code before performing checkpage
     */
    protected void checkPagePreparation() {
    }

    protected void pageLoaded() {

    }

    /**
     * Method entered when checkPage runs into an error (catching any throwable). You can throw a new throwable that
     * should be stacked onto the checkpage error (like new RuntimeException("bla", throwable) ).
     *
     * @param throwable .
     * @throws Throwable .
     */
    protected void checkPageErrorState(Throwable throwable) throws Throwable {
        throw throwable;
    }

    /**
     * Gets all @Check annotated fields of a class and executes a webdriver find().
     */
    private void checkAnnotatedFields(CheckRule checkRule) {
        List<Class<? extends AbstractPage>> allClasses = collectAllSuperClasses();

        allClasses.forEach(pageClass -> {
            for (Field field : pageClass.getDeclaredFields()) {
                field.setAccessible(true);
                List<AbstractFieldAction> fieldActions = getFieldActions(field, checkRule, this);
                fieldActions.forEach(AbstractFieldAction::run);
                field.setAccessible(false);
            }
        });
    }

    protected Optional<List<AbstractFieldAction>> addCustomFieldActions(Field field, AbstractPage declaringPage) {
        return Optional.empty();
    }

    private List<AbstractFieldAction> getFieldActions(Field field, CheckRule checkRule, AbstractPage declaringPage) {
        List<AbstractFieldAction> fieldActions = new ArrayList<>();

        SetNameFieldAction setNameFieldAction = new SetNameFieldAction(field, declaringPage);
        fieldActions.add(setNameFieldAction);

        addCustomFieldActions(field, declaringPage).ifPresent(fieldActions::addAll);

        GuiElementCheckFieldAction guiElementCheckFieldAction = new GuiElementCheckFieldAction(field, checkRule, declaringPage);
        fieldActions.add(guiElementCheckFieldAction);

        return fieldActions;
    }

    /**
     * Collects all classes in the hierarchy that are sub classes of {@link AbstractPage}
     */
    private List<Class<? extends AbstractPage>> collectAllSuperClasses() {
        final LinkedList<Class<? extends AbstractPage>> allClasses = new LinkedList<>();
        allClasses.add(this.getClass());
        boolean running = true;
        /*
         * Find all superclasses.
         */
        Class<?> clazz = this.getClass();
        while (running) {

            clazz = clazz.getSuperclass();

            try {
                if (clazz == null) {
                    running = false;
                } else if (clazz == AbstractComponent.class || clazz == Page.class) {
                    /*
                      When the class is on of the abstract implementations,
                      then stop searching here
                     */
                    break;
                } else {
                    @SuppressWarnings("unchecked") final Class<? extends AbstractPage> pageClass = (Class<? extends AbstractPage>) clazz;
                    allClasses.add(pageClass);
                }
            } catch (final ClassCastException e) {
                running = false;
            }
        }
        return allClasses;
    }

    @Override
    abstract public WebDriver getWebDriver();

    protected <T extends Component> T createComponent(Class<T> componentClass, UiElement rootElement) {
        return pageFactory.createComponent(componentClass, rootElement);
    }

    @Override
    public <T extends Page> T createPage(final Class<T> pageClass) {
        return pageFactory.createPage(pageClass, getWebDriver());
    }

    @Override
    public <T extends Page> Optional<T> waitForPage(final Class<T> pageClass, int seconds) {
        return pageFactory.waitForPage(pageClass, getWebDriver(), seconds);
    }
}
