## Create pages in tests

**API v1**
```java
class Test extends TesterraTest {
    @Test
    public void test() {
        MyPage page = PageFactory.create(MyPage.class, WebDriverManager.getWebDriver());
    }
}
```

**API v2**
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

**API v1**
```java
class MyPage extends Page {
    public OtherPage navigateToOtherPage() {
        return PageFactory.create(OtherPage.class, getWebDriver());
    }
}
```

**API v2**
```java
class MyPage extends Page {
    public OtherPage navigateToOtherPage() {
        // Uses same WebDriver
        return createPage(OtherPage.class);
    }
}
```


## Locate elements in pages

**API v1**
```java
class MyPage extends Page {
    private GuiElement element = new GuiElement(By.id("42"), getWebDriver());
}
```

**API v2**
```java
class MyPage extends Page {
    private UiElement element = findById(42);
}
```

## Assert conditionally that an element is displayed

**API v1**
```java
if (boolean) {
    element.asserts().assertIsDisplayed();
} else {
    element.asserts().assertIsNotDisplayed();
}
```

**API v2**
```java
element.expect().displayed(boolean);
```

## Assert conditionally that the page title ends with a text

**API v1**
```java
// No implicit retry
Assert.assertEquals(page.getWebDriver().getTitle().endsWith("Startseite"), boolean);
```

**API v2**
```java
page.expect().title().endsWith("Startseite").is(boolean);
```

## Improved logging

**API v1**
```text
By.className: button child of By.className: box text equals the requested text
 Expected: Anmelden
 Actual: Button1 expected [true] but found [false]
```

**API v2**
```text
Expected that WebTestPage -> inputForm -> submitButton text=[Button1] equals [Absenden]
```

## Assert the amount of found elements

**API v1**
```java
Assert.assertTrue(elements.getNumberOfFoundElements() >= 4 && elements.getNumberOfFoundElements() <= 6);
```

**API v2**
```java
elements.expect().foundElements().isBetween(4, 6);
```

## Value mapping

**API v1**

*unsupported*

**API v2**

```java
element.expect()
        .text()
        .map(String::trim)
        .map(String::toUpperCase)
        .is(String);
```

## Locate sub elements

**API v1**
```java
GuiElement parent = new GuiElement(By.id("42"), getWebDriver());
GuiElement sub = parent.getSubElement(By.xpath("//div[1]"));
```

**API v2**
```java
UiElement parent = findById(42);
UiElement sub = parent.find(By.xpath("//div[1]"));
```

## Elements in frames

**API v1**
```java
GuiElement frame = new GuiElement(By.tagName("frame"), getWebDriver());
GuiElement guiElement = new GuiElement(By.id("42"), getWebDriver(), frame);
```

**API v2**
```java
UiElement frame = find(By.tagName("frame"));
UiElement uiElement = frame.findById(42);
```

## Find elements over frames

**API v1**

*unsupported*

**API v2**
```java
findDeep(By.id("acccept-cookies")).click();
```

## Assert text on the last item in an element list

**API v1**
```java
elements.getList().get(elements.getNumberOfFoundElements() - 1).asserts().assertText("Third");
```

**API v2**
```java
elements.list().last().expect().text("Third");
```

## Collected and optional assertions

**API v1**
```java
element.assertCollector().assertIsDisplayed();
element.nonFunctionalAsserts().assertIsDisplayed();
```

**API v2**
```java
CONTROL.collectedAssertions(() -> {
    element.expect().displayed(true);
});

CONTROL.optionalAssertions(() -> {
    element.expect().displayed(true);
});
```

## Fast waits

**API v1**
```java
element.setElementTimeoutSeconds(0);
element.waits().waitForIsDisplayed();
```

**API v2**
```java
element.waitFor(0).displayed(true);
```

## Retry blocks and perform something on failure

**API v1**
```java
Timer timer = new Timer(500, 5000);
timer.executeSequence(new Timer.Sequence<Boolean>() {
    @Override
    public void run() throws Throwable {
        try {
            element.asserts().assertTextContains("geladen");
        } catch(AssertionError e) {
            element.getWebDriver().reload();
        }
    }
});
```

**API v2**
```java
CONTROL.retryFor(5, () -> {
    element.expect().text().contains("geladen");
}, () -> {
    element.getWebDriver().reload();    
});
```

## Writing xPathes

**API v1**
```java
By.xpath(String.format("//button[//span[contains(concat(' ', normalize-space(@class), ' '), ' info ') and starts-with(.//text(),'%s')]]"), "Klick mich"));
```

**API v2**
```java
XPath.from("button").encloses("span").classes("info").text().startsWith("Klick mich");
```

## Optional elements

**API v1**
```java
private Optional<GuiElement> getSectionBtn() {
    if (container.waits().waitForIsDisplayed()) {
        return Optional.of(container.getSubElement(By.tagName("button")));
    } else {
        return Optional.empty();
    }
}

getSectionBtn().ifPresent(guiElement -> {
    if (guiElement.waits().waitForIsDisplayed()) {
        // Optional element is displayed
    }
});
```

**API v2**
```java
private UiElement getSectionBtn() {
    if (container.waitFor().displayed(true)) {
        return container.find(By.tagName("button"));
    } else {
        return container.newEmpty();
    }
}

if (getSectionBtn().waitFor().displayed(true)) {
    // Optional element is displayed
}
```
