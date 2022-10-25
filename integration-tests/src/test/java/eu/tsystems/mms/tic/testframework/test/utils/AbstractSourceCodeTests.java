package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithNotExistingElement;
import eu.tsystems.mms.tic.testframework.report.model.context.ErrorContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.model.context.ScriptSource;
import eu.tsystems.mms.tic.testframework.testing.AssertProvider;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import io.testerra.test.pretest_status.TestStatusTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created on 2022-10-07
 *
 * @author mgn
 */
public class AbstractSourceCodeTests extends AbstractTestSitesTest implements TestStatusTest, AssertProvider, PropertyManagerProvider, PageFactoryProvider {

    protected void doSimpleFail() {
        ASSERT.fail("Simple failed");
    }

    protected void doPageFactoryFail() {
        PAGE_FACTORY.createPage(PageWithNotExistingElement.class, this.getWebDriver());
    }

    protected void verifySourceCodeSnippet(String testMethodName, String fileName, String codeLine) {
        List<ScriptSource> scriptSources = this.findMethodContexts(testMethodName)
                .flatMap(MethodContext::readErrors)
                .map(ErrorContext::getScriptSource)
                .flatMap(Optional::stream)
                .filter(scriptSource -> scriptSource.getFileName().equals(fileName))
                .collect(Collectors.toList());
        ASSERT.assertEquals(scriptSources.size(), 1, "Error context should contains script sources of " + fileName);

        ScriptSource source = scriptSources.get(0);
        List<ScriptSource.Line> lines = source.readLines()
                .filter(line -> line.getLineNumber() == source.getMarkedLineNumber())
                .filter(line -> line.getLine().contains(codeLine))
                .collect(Collectors.toList());
        ASSERT.assertEquals(lines.size(), 1, String.format("The line '%s' should part of source code snippet.", codeLine));
    }

}
