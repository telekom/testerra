package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import java.util.Optional;

public interface PageCreator {
    default <T extends Page> Optional<T> waitForPage(Class<T> pageClass) {
        return waitForPage(pageClass, -1);
    }
    <T extends Page> Optional<T> waitForPage(Class<T> pageClass, int seconds);
    <T extends Page> T createPage(final Class<T> pageClass);
}
