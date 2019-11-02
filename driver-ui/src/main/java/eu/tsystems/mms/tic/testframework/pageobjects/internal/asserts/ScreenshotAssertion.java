package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import com.google.inject.Inject;
import eu.tsystems.mms.tic.testframework.report.IReport;

import java.io.File;

public class ScreenshotAssertion extends ImageAssertion {

    @Inject
    private IReport report;

    public ScreenshotAssertion(PropertyAssertion parentAssertion, AssertionProvider<File> provider) {
        super(parentAssertion, provider);
    }
}
