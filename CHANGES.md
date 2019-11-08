# New API Changes

## Pages implementation

The new standard implementation of Pages is now
```java
class MyPage extends FluentPage<MyPage> {
    public MyPage(WebDriver webDriver) {
        super(webDriver);
    }
    
    protected MyPage self() {
        return this;
    }
}
```

## Pages instantiation

The new standard way to instantiate Pages is now
```java
class MyTest extends TesterraTest {
    
    @Test
    public void test_MyPageTest() {
        MyPage page = pageFactory.create(MyPage.class);
        
        // Or with different WebDriver
        MyPage page = pageFactory.create(MyPage.class, WebDriverManager.getWebDriver());
    }
}
```

Using the static PageFactory is `@deprecated`
```java
MyPage page = PageFactory.create(MyPage.class, WebDriverManager.getWebDriver());
```

Constructor instantiation of Pages is `FORBIDDEN`
```java
MyPage page = new MyPage(WebDriverManager.getWebDriver());
```

## Implicit Element checks

The standard way of implicit GuiElement checks is now
```java
class MyPage extends FluentPage<MyPage> {
    
    @Check
    private IGuiElement guiElement = findOneById(42);
}
```

Performing own page checks is `FORBIDDEN`
```java
class MyPage extends FluentPage<MyPage> {

    private IGuiElement guiElement = findOneById(42);
    
    public MyPage(WebDriver webDriver) {
        super(webDriver);
        
        // forbidden
        checkPage();
    }
}
```

## Element instantiation

The new standard way to instantiate GuiElements is now
```java
class MyPage extends FluentPage<MyPage> {
    private IGuiElement guiElement = findOneById(42);
    private IGuiElement guiElement = findOne(By.xpath("//div[1]"));
}
```

Constructor instantiation of GuiElements is now `@deprecated`

```java
class MyPage extends Page {
    private GuiElement guiElement = new GuiElement(By.id(42), driver);
    private GuiElement guiElement = new GuiElement(By.xpath("//div[1]"), driver);
}
```

For sub elements
```java
class MyPage extends FluentPage<MyPage> {
    private IGuiElement guiElement = findOneById(42);
    private IGuiElement subElement = withAncestor(guiElement).findOne(By.xpath("//div[1]"));
}
```

Retrieving sub elements from GuiElement is now `@deprecated`
```java
class MyPage extends FluentPage<MyPage> {
    private IGuiElement guiElement = new GuiElement(By.id(42), driver);
    private IGuiElement subElement = guiElement.getSubElement(By.xpath("//div[1]"));
}
```

For elements in frames
```java
class MyPage extends FluentPage<MyPage> {
    private IGuiElement frame = findOne(By.tagName("frame"));
    private IGuiElement guiElement = inFrame(frame).findOneById(14);
}
```

Passing frames to the constructor is now `@deprecated`
```java
class MyPage extends Page {
    private GuiElement frame = new GuiElement(By.tagName("frame"), driver);
    private GuiElement guiElement = new GuiElement(By.id(14), driver, frame);
}
```

## Assertions

The new standard way for performing assertions is now
```java
assertion.assertTrue(false);
```

Using the TestNG `Assert` is not recommended
```java
Assert.assertTrue(false);
```

## Collected Assertions

The new standard way to collect assertions of GuiElements is now
```java
collectAssertions(() -> guiElement.displayed().isTrue());
```

For many GuiElements or Pages
```java
collectAssertions(() -> {
    MyPage page = pageFactory.create(MyPage.class);
    page.title().is("TestPage");

    page.element().value().contains("Hello");
});
```

For custom assertions
```java
collectAssertions(() -> {
    String data = loadSomeData();
    assertion.assertEquals(data, "Hello World", "some data");
});
```

For other test methods
```java
@Test
public void test_CollectEverything() {
    collectAssertions(() -> test_TestSomething());
}
```

Using the static `AssertCollector` is now `@deprecated`
```java
AssertCollector.assertTrue(false);
```

Using the GuiElement's assert collector is now `@deprecated`
```java
guiElement.assertCollector().assertIsDisplayed();
```

Forcing standard assertions is now `@deprecated`
```java
page.forceGuiElementStandardAsserts();
```

Setting collected assertions by default is now `@deprecated`
```properties
tt.guielement.default.assertcollector=true
```

## Non Functional Assertions

The new standard way for non functional assertions works like collected assertions
```java
nonFunctional(() -> guiElement.displayed().isTrue());
```

Using the static `NonFunctionalAssert` is now `@deprecated`
```java
NonFunctionalAssert.assertTrue(false);
```

Using the GuiElement's non functional asserts are now `@deprecated`
```java
guiElement.nonFunctionalAsserts().assertIsDisplayed();
```

## Timeouts

The new standard way for setting timeouts is now

```java
withTimeout(1, () -> guiElement.displayed().isTrue());
```

For many GuiElements or Pages
```java
withTimeout(1, () -> {
    MyPage page = pageFactory.create(MyPage.class);
    page.title().is("TestPage");

    page.element().value().contains("Hello");
});
```

For the whole Page
```java
@PageOptions(elementTimeoutInSeconds = 1)
class MyPage extends FluentPage<MyPage> {
}
```

For other test methods
```java
@Test
public void test_TestSomething_fast() {
    withTimeout(1, () -> test_TestSomething());
}
```

Setting explicit timeouts on the Page is now `@deprecated`
```java
page.setElementTimeoutInSeconds(1);
```

Setting and restoring explicit timeouts on the GuiElement is now `@deprecated`
```java
guiElement.setTimeoutInSeconds(1);
guiElement.restoreDefaultTimeout();
```

Setting thread local timeouts using static `POConfig` is now `@deprecated`
```java
POConfig.setThreadLocalUiElementTimeoutInSeconds(1);
```

## Sub Pages
The new standard way to implement Sub Pages aka Components is
```java
public class MyComponent extends Component<MyComponent> {

    public MyComponent(IGuiElement rootElement) {
        super(rootElement);
    }

    @Override
    protected MyComponent self() {
        return this;
    }
}
```
Instantiate components
```java
class MyPage extends FluentPage<MyPage> {
    private MyComponent component = withAncestor(By.tagName("form")).createComponent(MyComponent.class);
}
```

