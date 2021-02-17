## Create pages in tests

**API v1**:
```java
class Test extends TesterraTest {
    @Test
    public void test() {
        MyPage page = PageFactory.create(MyPage.class, WebDriverManager.getWebDriver());
    }
}
```

**API v2**:
```java
class Test extends TesterraTest implements PageFactoryProvider {
    @Test
    public void test() {
        // Uses WEB_DRIVER_MANAGER.getWebDriver()
        MyPage page = PAGE_FACTORY.createPage(MyPage.class);
    }
}
```

## Create pages in pages

**API v1**:
```java
class MyPage extends Page {
    public OtherPage navigateToOtherPage() {
        return PageFactory.create(OtherPage.class, getWebDriver());
    }
}
```

**API v2**:
```java
class MyPage extends Page {
    public OtherPage navigateToOtherPage() {
        // Uses same WebDriver
        return createPage(OtherPage.class);
    }
}
```


## Locate elements in pages

**API v1**:
```java
class MyPage extends Page {
    private GuiElement element = new GuiElement(By.id("42"), getWebDriver());
}
```

**API v2**:
```java
class MyPage extends Page {
    private UiElement element = findById(42);
}
```

## Assert conditionally that an element is displayed

**API v1**:
```java
if (boolean) {
    element.asserts().assertIsDisplayed();
} else {
    element.asserts().assertIsNotDisplayed();
}
```

**API v2**:
```java
element.expect().displayed(boolean);
```

## Assert conditionally that the page title ends with a text

**API v1**:
```java
Assert.assertEquals(page.getWebDriver().getTitle().endsWith("Startseite"), boolean);
```

**API v2**:
```java
page.expect().title().endsWith("Startseite").is(boolean);
```

## Wait for the page URL to change

**API v1**:
*unsupported*

**API v2**:
```java
if (page.waitFor().url().startsWith("http://redirect.to").is(true)) {

}
```

## Assert the amount of found elements

**API v1**:
```java
Assert.assertTrue(elements.getNumberOfFoundElements() >= 4 && elements.getNumberOfFoundElements() <= 6);
```

**API v2**:
```java
elements.expect().numberOfElements().isBetween(4, 6);
```

## Value mapping

**API v1**:

*unsupported*

**API v2**:

```java
element.expect()
        .text()
        .map(String::trim)
        .map(String::toUpperCase)
        .is(String);
```

## Locate sub elements

**API v1**:
```java
GuiElement parent = new GuiElement(By.id("42"), getWebDriver());
GuiElement sub = parent.getSubElement(By.xpath("//div[1]"));
```

**API v2**:
```java
UiElement parent = findById(42);
UiElement sub = parent.find(By.xpath("//div[1]"));
```

## Elements in frames

**API v1**:
```java
GuiElement frame = new GuiElement(By.tagName("frame"), getWebDriver());
GuiElement guiElement = new GuiElement(By.id("42"), getWebDriver(), frame);
```

**API v2**:
```java
UiElement frame = find(By.tagName("frame"));
UiElement uiElement = frame.findById(42);
```

## Find elements over frames

**API v1**:
*unsupported*

**API v2**:
```java
findDeep(By.id("acccept-cookies")).click();
```

## Assert text on the last item in an element list

**API v1**:
```java
elements.getList().get(elements.getNumberOfFoundElements() - 1).asserts().assertText("Third");
```

**API v2**:
```java
elements.list().last().expect().text("Third");
```

## Collected and optional assertions

**API v1**:
```java
element.assertCollector().assertIsDisplayed();
element.nonFunctionalAsserts().assertIsDisplayed();
```

**API v2**:
```java
CONTROL.collectedAssertions(() -> {
    element.expect().displayed(true);
});

CONTROL.optionalAssertions(() -> {
    element.expect().displayed(true);
});
```

## Fast assertions

**API v1**:
```java
element1.setElementTimeoutSeconds(0);
element1.asserts().assertIsDisplayed();

element2.setElementTimeoutSeconds(0);
element2.asserts().assertIsDisplayed();
```

**API v2**:
```java
CONTROL.withTimeout(0, () -> {
    element1.expect().displayed(true);
    element2.expect().displayed(true);
});
```

## Retry blocks

**API v1**:
```java
Timer timer = new Timer(500, 15_000);
timer.executeSequence(new Timer.Sequence<Boolean>() {
    @Override
    public void run() throws Throwable {
        // sequence code here
    }
});
```

**API v2**:
```java
CONTROL.retryFor(5, () -> {
    button.click();
    button.expect().enabled(false);
});
```

## Writing xPathes

**API v1**:
```java
GuiElement element = new GuiElement(By.xpath(String.format("//button[descendant::span[.//text()='%s']]"), "Klick mich"), getWebDriver());
```

**API v2**:
```java
UiElement element = find(XPath.from("button").encloses("span").text("Klick mich"))
```
