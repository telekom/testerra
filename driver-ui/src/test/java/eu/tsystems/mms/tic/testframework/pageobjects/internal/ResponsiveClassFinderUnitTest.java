package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class ResponsiveClassFinderUnitTest {
    private static final String GROUP_CLASS = ".class";
    private static final String GROUP_OTHER = "other";

    @Test
    void test_filterConfigure_yieldSameResults() {
        final var reflectionsConfigure = new Reflections(ResponsiveClassFinder.configure());
        final var reflectionsFilter = new Reflections(ResponsiveClassFinder.filter(ResponsiveClassFinder.configure()));

        final var resultsConfigure = reflectionsConfigure.getSubTypesOf(Page.class);
        final var resultsFilter = reflectionsFilter.getSubTypesOf(Page.class);

        Assert.assertFalse(resultsConfigure.isEmpty(), "There should be results."); // Note: Does not work when executing manually! (Because ClassLoader only contains test class and its dependencies.)
        Assert.assertEquals(resultsConfigure, resultsFilter);
    }

    /**
     * Use this test to check what files the {@link ConfigurationBuilder} for {@link ResponsiveClassFinder} considers potential sources.
     * Only sensible for manual use right now, so keep disabled otherwise.
     */
    @SuppressWarnings("unused")
    @Test(enabled = false)
    void testReflections() {
        final var configuration = ResponsiveClassFinder.configure();
        final HashMap<String, Integer> grouping = new HashMap<>();
        configuration.filterInputsBy(name -> {
            final var lastDot = name.lastIndexOf(".");
            grouping.compute(lastDot >= 0 ? name.substring(lastDot) : StringUtils.EMPTY, (key, value) -> {
                if (value == null) {
                    value = 0;
                }
                return 1 + value;
            });
            return true;
        });
        final var reflections = new Reflections(configuration);
        final var results = reflections.getSubTypesOf(Page.class);

        final var comparison = grouping.entrySet().stream().<Map<String, Integer>>collect(HashMap::new,
                (map, entry) -> map.merge(entry.getKey().equals(GROUP_CLASS) ? GROUP_CLASS : GROUP_OTHER, entry.getValue(), Integer::sum),
                Map::putAll);

        // set breakpoint here
        final var countClass = comparison.get(GROUP_CLASS);
        final var countOther = comparison.get(GROUP_OTHER);
        System.out.printf("'%s' entries found: %d\n%s entries found: %d\nRatio of non-class to class entries: %f%n",
                GROUP_CLASS, countClass, GROUP_OTHER, countOther, countOther.floatValue() / countClass.floatValue());
    }
}