package eu.tsystems.mms.tic.testframework.pageobjects;

public interface IPage extends WebDriverRetainer {
    IPage setElementTimeoutInSeconds(int newElementTimeout);
    int getElementTimeoutInSeconds();
}
