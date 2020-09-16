package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.NonUniqueElementException;
import org.openqa.selenium.WebElement;

/**
 * Interface for classes that contain an {@link WebElement}
 * @author Mike Reiche
 */
public interface WebElementRetainer {
    /**
     * Does the same like {@link #findWebElement()}
     */
    @Deprecated
    default WebElement getWebElement() {
        return findWebElement();
    }

    /**
     * @return The first found filtered {@link WebElement}
     * @throws ElementNotFoundException If none found
     * @throws NonUniqueElementException If more than one WebElement has been found according to given {@link Locate}
     */
    WebElement findWebElement();
}
