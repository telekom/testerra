package eu.tsystems.mms.tic.testframework.pageobjects.image;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IAssertableQuantifiedValue;
import org.openqa.selenium.TakesScreenshot;

public interface IShot<T extends TakesScreenshot> {
    IAssertableQuantifiedValue<Double, T> distance(final String referenceImageName);
}
