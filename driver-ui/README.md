# jfennec driver-ui
jfennec driver-ui is the base module for ui driven tests. All modules with ui implementations rely on this module, as it
provides the WebDriverManager and GuiElement and the Page class for the page object pattern, plus PageFactory. 
This module is the scripting base - without specifying a driver implementation.  

## Installation / Usage

For maven:

```xml
<dependencies>
    <dependency>
        <groupId>eu.tsystems.mms.tic.jfennec</groupId>
        <artifactId>driver-ui</artifactId>
        <version>1-SNAPSHOT</version>
    </dependecy>
</dependencies>
```

For gradle:
```text
compile 'eu.tsystems.mms.tic.jfennec:driver-ui:1-SNAPSHOT'
```

###### Using driver-ui module:

With this module you are able to script your ui tests:

```java
public class MyTest extends FennecTest {
    
    @Test
    public void testT01_My_first_fennec_test() {
        WebDriver driver = WebDriverManager.getWebDriver();
        MyPage myPage = PageFactory.create(MyPage.class, driver);
        ...
    }
}
```

***

Documentation pending