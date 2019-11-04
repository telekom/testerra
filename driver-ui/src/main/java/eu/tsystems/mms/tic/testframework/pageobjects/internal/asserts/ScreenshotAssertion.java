package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.report.IReport;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;

import java.io.File;

public class ScreenshotAssertion extends ImageAssertion implements IScreenshotAssertion {

    private static IReport report = Testerra.ioc().getInstance(IReport.class);

    private final AssertionProvider<Screenshot> providerOverride;

    public ScreenshotAssertion(PropertyAssertion parentAssertion, AssertionProvider<Screenshot> provider) {
        super(parentAssertion, new AssertionProvider<File>() {
            @Override
            public File getActual() {
                return provider.getActual().getScreenshotFile();
            }

            @Override
            public String getSubject() {
                return String.format("%s.screenshot", provider.getSubject());
            }
        });
        providerOverride = provider;
    }

    @Override
    public IScreenshotAssertion toReport() {
        report.addScreenshot(providerOverride.getActual(), IReport.Mode.COPY);
        return this;
    }
}
