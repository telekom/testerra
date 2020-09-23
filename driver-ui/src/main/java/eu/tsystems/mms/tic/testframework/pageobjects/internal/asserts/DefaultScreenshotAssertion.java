package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import java.io.File;

public class DefaultScreenshotAssertion extends DefaultImageAssertion implements ScreenshotAssertion {

    private static Report report = Testerra.injector.getInstance(Report.class);

    private final AssertionProvider<Screenshot> providerOverride;

    public DefaultScreenshotAssertion(AbstractPropertyAssertion parentAssertion, AssertionProvider<Screenshot> provider) {
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
    public ScreenshotAssertion toReport() {
        report.addScreenshot(providerOverride.getActual(), Report.Mode.COPY);
        return this;
    }
}
