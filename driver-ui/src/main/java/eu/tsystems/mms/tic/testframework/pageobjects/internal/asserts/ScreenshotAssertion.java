package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import com.google.inject.Inject;
import eu.tsystems.mms.tic.testframework.report.IReport;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;

import java.io.File;
import java.io.IOException;

public class ScreenshotAssertion extends ImageAssertion implements IScreenshotAssertion {

    @Inject
    private IReport report;

    public ScreenshotAssertion(PropertyAssertion parentAssertion, AssertionProvider<File> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public IScreenshotAssertion toReport() {
        try {
            Screenshot screenshot = report.provideScreenshot(provider.actual(), null, IReport.Mode.MOVE);
            report.addScreenshot(screenshot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
