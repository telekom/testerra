## Creating pages in tests

### API v1
```java
class Test extends TesterraTest {
    @Test
    public void test() {
        MyPage page = PageFactory.createPage(MyPage.class, WebDriverManager.getWebDriver());
    }
}
```

### API v2
```java
class Test extends TesterraTest implements PageFactoryProvider {
    @Test
    public void test() {
        MyPage page = pageFactory.createPage(MyPage.class);
    }
}
```

## Creating pages in pages

### API v1
```java
class MyPage extends Page {
    public OtherPage navigateToOtherPage() {
        return PageFactory.createPage(OtherPage.class, getWebDriver());
    }
}
```

### API v2
```java
class MyPage extends Page {
    public OtherPage navigateToOtherPage() {
        return createPage(OtherPage.class);
    }
}
```

## Creating elements in pages

### API v1
```java
class MyPage extends Page {
    private GuiElement element = new GuiElement(By.id("42"), getWebDriver());
}
```

### API v2
```java
class MyPage extends Page {
    private UiElement element = findById(42);
}
```

## Assert conditionally that an element is displayed

### API v1
```java
if (condition) {
    element.asserts().assertIsDisplayed();
} else {
    element.asserts().assertIsNotDisplayed();
}
```

### API v2
```java
element.expect().displayed(condition);
```

## Find sub elements

### API v1
```java
GuiElement parent = new GuiElement(By.id("42"), getWebDriver());
GuiElement sub = parent.getSubElement(By.xpath("//div[1]"));
```

### API v2
```java
UiElement parent = findById(42);
UiElement sub = parent.find(By.xpath("//div[1]"));
```

## Elements based on frames

### API v1
```java
GuiElement frame = new GuiElement(By.tagName("frame"), getWebDriver());
GuiElement guiElement = new GuiElement(By.id("42"), getWebDriver(), frame);
```

### API v2
```java
UiElement frame = find(By.tagName("frame"));
UiElement uiElement = frame.findById(42);
```

## Find elements over frames

### API v1
*unsupported*

### API v2
```java
class MyPage extends Page {
    public void acceptCookies() {
        findDeep(By.id("acccept")).click();
    }
}
```

## Assert text on the last in an element list

### API v1
```java
elements.getList().get(elements.getNumberOfFoundElements() - 1).asserts().assertText("Third");
```

### API v2
```java
elements.list().last().expect().text("Third");
```

## Collected and optional assertions

### API v1
```java
element.assertCollector().assertIsDisplayed();
element.nonFunctionalAsserts().assertIsDisplayed();
```

### API v2
```java
Control.collectedAssertions(() -> {
    element.expect().displayed(true);
});

Control.optionalAssertions(() -> {
    element.expect().displayed(true);
});
```

## Access WebElement

### API v1
```java
WebElement webElement = element.getWebElement();
```

### API v2
```java
element.findWebElement(webElement -> {
});
```

## Writing xPathes

### API v1
```java
GuiElement element = new GuiElement(By.xpath(String.format("//button[descendant::span[.//text()='%s']]"), "Klick mich"));
```

### API v2
```java
UiElement element = find(XPath.from("button").encloses("span").text("Klick mich"))
```


