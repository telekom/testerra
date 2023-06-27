package io.testerra.report.test.report_test.extensions;

import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import io.testerra.report.test.AbstractReportTest;
import io.testerra.report.test.pages.ReportSidebarPageType;
import io.testerra.report.test.pages.report.sideBarPages.ReportDashBoardPage;
import io.testerra.report.test.pages.report.sideBarPages.ReportTestsPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created on 2023-03-23
 *
 * @author mgn
 */
public class ClassContextTest extends AbstractReportTest {

    @DataProvider(name = "classContextProvider")
    public Iterator<TestContainer> classContextProvider() {
        List<TestContainer> classContexts = new ArrayList<>();

        classContexts.add(new TestContainer("ClassContextInheritedClasses", 3, List.of("testTestMethodInChildClasses", "testTestMethodInParentClasses")));
        classContexts.add(new TestContainer("ClassContextDifferentClasses", 2, List.of("testTestMethodInDifferentClasses1", "testTestMethodInDifferentClasses2")));
        classContexts.add(new TestContainer("ClassContextSameClassPack1", 1, List.of("testTestMethodInSameClassesPack1")));
        classContexts.add(new TestContainer("ClassContextSameClassPack2", 1, List.of("testTestMethodInSameClassesPack2")));

        return classContexts.iterator();
    }

    @Test(dataProvider = "classContextProvider")
    public void testT01_classContextClassesView(TestContainer testContainer) {
//        String classContextName = "ClassContextInheritedClasses";
//        int countMethods = 3;
//        List<String> methods = List.of("testTestMethodInChildClasses", "testTestMethodInParentClasses");

        TestStep.begin("Navigate to tests page.");
        ReportDashBoardPage reportDashBoardPage = this.gotoDashBoardOnAdditionalReport();
        ReportTestsPage reportTestsPage = reportDashBoardPage.gotoToReportPage(ReportSidebarPageType.TESTS, ReportTestsPage.class);

        reportTestsPage = reportTestsPage.selectClassName(testContainer.classContextName);
        ASSERT.assertEquals(reportTestsPage.getColumnWithoutHead(ReportTestsPage.TestsTableEntry.METHOD).size(), testContainer.countMethods);
        reportTestsPage.assertMethodColumnContainsCorrectMethods(testContainer.methods);
    }

    class TestContainer {
        public String classContextName;
        public int countMethods;
        public List<String> methods;

        public TestContainer(String classContextName, int countMethods, List<String> methods) {
            this.classContextName = classContextName;
            this.countMethods = countMethods;
            this.methods = methods;
        }
    }

}
