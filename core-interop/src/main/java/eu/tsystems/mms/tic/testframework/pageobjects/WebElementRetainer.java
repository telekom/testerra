package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.NonUniqueElementException;
import java.util.function.Consumer;
import org.openqa.selenium.WebElement;

/**
 * Interface for classes that contain an {@link WebElement}
 * @author Mike Reiche
 */
public interface WebElementRetainer {
    /**
     * Supplies the first found filtered {@link WebElement} to a consumer.
     * This makes sure that the element is present it it's current scope and not stale
     * @throws ElementNotFoundException If none found
     * @throws NonUniqueElementException If more than one WebElement has been found according to given {@link Locate}
     */
    void findWebElement(Consumer<WebElement> consumer);
}
