package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.internal.Nameable;
import eu.tsystems.mms.tic.testframework.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.FileAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.PatternAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.StringAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.AbstractUiElementList;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.HorizontalDistanceAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.RectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.UiElementAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.VerticalDistanceAssertion;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.webdriver.IWebDriverManager;
import java.awt.Color;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EmptyUiElement implements UiElement {
    private String name;
    private final Nameable parent;
    private static final EmptyStringAssertion emptyStringAssertion = new EmptyStringAssertion<>();
    private static final EmptyFileAssertion emptyFileAssertion = new EmptyFileAssertion();
    private static final EmptyImageAssertion emptyImageAssertion = new EmptyImageAssertion();
    private static final EmptyRectAssertion emptyRectAssertion = new EmptyRectAssertion();
    private static final EmptyUiElementAssertion emptyUiElementAssertion = new EmptyUiElementAssertion();
    private static class EmptyBinaryAssertion<T> implements BinaryAssertion<T> {
        @Override
        public T getActual() {
            return null;
        }

        @Override
        public boolean is(boolean expected, String subject) {
            return false;
        }
    }
    private static class EmptyQuantityAssertion<T> extends EmptyBinaryAssertion<T> implements QuantityAssertion<T> {

        @Override
        public boolean is(Object expected, String subject) {
            return false;
        }

        @Override
        public boolean isNot(Object expected, String subject) {
            return false;
        }

        @Override
        public boolean isGreaterThan(BigDecimal expected, String subject) {
            return false;
        }

        @Override
        public boolean isLowerThan(BigDecimal expected, String subject) {
            return false;
        }

        @Override
        public boolean isGreaterEqualThan(BigDecimal expected, String subject) {
            return false;
        }

        @Override
        public boolean isLowerEqualThan(BigDecimal expected, String subject) {
            return false;
        }

        @Override
        public boolean isBetween(BigDecimal lower, BigDecimal higher, String subject) {
            return false;
        }

        @Override
        public <MAPPED_TYPE> StringAssertion<MAPPED_TYPE> map(Function<? super T, MAPPED_TYPE> mapFunction) {
            return emptyStringAssertion;
        }

        @Override
        public QuantityAssertion<BigDecimal> absolute() {
            return emptyStringAssertion;
        }
    }
    private static class EmptyStringAssertion<T> extends EmptyQuantityAssertion<T> implements StringAssertion<T> {
        @Override
        public BinaryAssertion<Boolean> contains(String expected) {
            return emptyStringAssertion;
        }

        @Override
        public BinaryAssertion<Boolean> startsWith(String expected) {
            return emptyStringAssertion;
        }

        @Override
        public BinaryAssertion<Boolean> endsWith(String expected) {
            return emptyStringAssertion;
        }

        @Override
        public PatternAssertion matches(Pattern pattern) {
            return new EmptyPatternAssertion();
        }

        @Override
        public BinaryAssertion<Boolean> hasWords(List<String> words) {
            return emptyStringAssertion;
        }

        @Override
        public QuantityAssertion<Integer> length() {
            return emptyStringAssertion;
        }
    }
    private static class EmptyPatternAssertion extends EmptyBinaryAssertion<Matcher> implements PatternAssertion {}
    private static class EmptyFileAssertion implements FileAssertion {

        @Override
        public File getActual() {
            return null;
        }

        @Override
        public QuantityAssertion<Long> bytes() {
            return emptyStringAssertion;
        }

        @Override
        public StringAssertion<String> name() {
            return emptyStringAssertion;
        }

        @Override
        public StringAssertion<String> extension() {
            return emptyStringAssertion;
        }

        @Override
        public StringAssertion<String> mimetype() {
            return emptyStringAssertion;
        }

        @Override
        public BinaryAssertion<Boolean> exists() {
            return emptyStringAssertion;
        }
    }
    private static class EmptyImageAssertion implements ImageAssertion {

        @Override
        public File getActual() {
            return null;
        }

        @Override
        public QuantityAssertion<Double> pixelDistance(String referenceImageName) {
            return emptyStringAssertion;
        }

        @Override
        public FileAssertion file() {
            return emptyFileAssertion;
        }
    }
    private static class EmptyRectAssertion implements RectAssertion {
        private static final EmptyHorizontalAssertion emptyHorizontalAssertion = new EmptyHorizontalAssertion();
        private static final EmptyVerticalAssertion emptyVerticalAssertion = new EmptyVerticalAssertion();
        private static class EmptyHorizontalAssertion implements HorizontalDistanceAssertion {

            @Override
            public Integer getActual() {
                return null;
            }

            @Override
            public QuantityAssertion<Integer> toRightOf(TestableUiElement guiElement) {
                return emptyStringAssertion;
            }

            @Override
            public QuantityAssertion<Integer> toLeftOf(TestableUiElement guiElement) {
                return emptyStringAssertion;
            }
        }
        private static class EmptyVerticalAssertion implements VerticalDistanceAssertion {

            @Override
            public Integer getActual() {
                return null;
            }

            @Override
            public QuantityAssertion<Integer> toTopOf(TestableUiElement guiElement) {
                return emptyStringAssertion;
            }

            @Override
            public QuantityAssertion<Integer> toBottomOf(TestableUiElement guiElement) {
                return emptyStringAssertion;
            }
        }

        @Override
        public Rectangle getActual() {
            return null;
        }

        @Override
        public BinaryAssertion<Boolean> contains(TestableUiElement guiElement) {
            return emptyStringAssertion;
        }

        @Override
        public BinaryAssertion<Boolean> intersects(TestableUiElement guiElement) {
            return emptyStringAssertion;
        }

        @Override
        public BinaryAssertion<Boolean> leftOf(TestableUiElement guiElement) {
            return emptyStringAssertion;
        }

        @Override
        public BinaryAssertion<Boolean> rightOf(TestableUiElement guiElement) {
            return emptyStringAssertion;
        }

        @Override
        public BinaryAssertion<Boolean> above(TestableUiElement guiElement) {
            return emptyStringAssertion;
        }

        @Override
        public BinaryAssertion<Boolean> below(TestableUiElement guiElement) {
            return emptyStringAssertion;
        }

        @Override
        public HorizontalDistanceAssertion fromRight() {
            return emptyHorizontalAssertion;
        }

        @Override
        public HorizontalDistanceAssertion fromLeft() {
            return emptyHorizontalAssertion;
        }

        @Override
        public VerticalDistanceAssertion fromTop() {
            return emptyVerticalAssertion;
        }

        @Override
        public VerticalDistanceAssertion fromBottom() {
            return emptyVerticalAssertion;
        }
    }
    private static class EmptyUiElementAssertion implements UiElementAssertion {

        @Override
        public ImageAssertion screenshot(Report.Mode reportMode) {
            return emptyImageAssertion;
        }

        @Override
        public StringAssertion<String> text() {
            return emptyStringAssertion;
        }

        @Override
        public StringAssertion<String> attribute(String attribute) {
            return emptyStringAssertion;
        }

        @Override
        public StringAssertion<String> css(String property) {
            return emptyStringAssertion;
        }

        @Override
        public BinaryAssertion<Boolean> enabled() {
            return emptyStringAssertion;
        }

        @Override
        public BinaryAssertion<Boolean> selected() {
            return emptyStringAssertion;
        }

        @Override
        public BinaryAssertion<Boolean> selectable() {
            return emptyStringAssertion;
        }

        @Override
        public QuantityAssertion<Integer> foundElements() {
            return emptyStringAssertion;
        }

        @Override
        public BinaryAssertion<Boolean> present() {
            return emptyStringAssertion;
        }

        @Override
        public BinaryAssertion<Boolean> displayed() {
            return emptyStringAssertion;
        }

        @Override
        public BinaryAssertion<Boolean> visible(boolean complete) {
            return emptyStringAssertion;
        }

        @Override
        public StringAssertion<String> tagName() {
            return emptyStringAssertion;
        }

        @Override
        public RectAssertion bounds() {
            return emptyRectAssertion;
        }
    }

    public EmptyUiElement(Nameable parent) {
        this.parent = parent;
    }

    @Override
    public UiElement setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getName(boolean detailed) {
        String name = this.getClass().getSimpleName();
        if (this.name != null) {
            name += "("+this.name+")";
        }
        return name;
    }

    @Override
    public Nameable getParent() {
        return this.parent;
    }

    @Override
    public boolean hasName() {
        return this.name != null;
    }

    @Override
    public void screenshotToReport() {

    }

    @Override
    public UiElementAssertion waitFor(int seconds) {
        return emptyUiElementAssertion;
    }

    @Override
    public UiElementAssertion expect() {
        return emptyUiElementAssertion;
    }

    @Override
    public String createXPath() {
        return "";
    }

    @Override
    public Locator getLocator() {
        return LOCATE.by(By.tagName("empty"));
    }

    @Override
    public UiElementList<UiElement> list() {

        EmptyUiElement emptyUiElement = this;
        return new AbstractUiElementList<UiElement>(emptyUiElement) {
            @Override
            public UiElement get(int i) {
                return emptyUiElement;
            }
        };
    }

    @Override
    public UiElement shadowRoot() {
        return this;
    }

    @Override
    public UiElement find(Locator locator) {
        return this;
    }

    @Override
    public InteractiveUiElement scrollIntoView(Point offset) {
        return this;
    }

    @Override
    public InteractiveUiElement highlight(Color color) {
        return this;
    }

    @Override
    public void findWebElement(Consumer<WebElement> consumer) {

    }

    @Override
    public InteractiveUiElement click() {
        return this;
    }

    @Override
    public InteractiveUiElement doubleClick() {
        return this;
    }

    @Override
    public InteractiveUiElement contextClick() {
        return this;
    }

    @Override
    public InteractiveUiElement select() {
        return this;
    }

    @Override
    public InteractiveUiElement deselect() {
        return this;
    }

    @Override
    public InteractiveUiElement sendKeys(CharSequence... charSequences) {
        return this;
    }

    @Override
    public InteractiveUiElement type(String text) {
        return this;
    }

    @Override
    public InteractiveUiElement clear() {
        return this;
    }

    @Override
    public InteractiveUiElement hover() {
        return this;
    }

    @Override
    public InteractiveUiElement submit() {
        return this;
    }

    @Override
    public WebDriver getWebDriver() {
        return Testerra.getInjector().getInstance(IWebDriverManager.class).getWebDriver();
    }
}
