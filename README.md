# testerra
testerra is an open source test automation library for web frontend testing. It provides a tool suite for many use cases: 
a base API for Page Object Pattern (including responsive layouts) and GuiElements (smarter WebElements (Selenium)), 
enhanced reporting functionality, a utility collection and some additional helpful modules.

## Main modules

Framework core:
* [core](core/README.md)
* ([core-interop](core-interop/README.md))

UI Testing modules:
* [driver-ui](driver-ui/README.md)
* [driver-ui-desktop](driver-ui-desktop/README.md)

Connectors:
* [console-connector](console-connector/README.md)
* [db-connector](db-connector/README.md)
* [mail-connector](mail-connector/README.md)

Additional modules:
* [bmp](bmp/README.md)

## Installation / Usage

At least you have to put the core module into your project dependencies:

For maven:

```xml
<dependencies>
    <dependency>
        <groupId>eu.tsystems.mms.tic.testerra</groupId>
        <artifactId>core</artifactId>
        <version>1-SNAPSHOT</version>
    </dependecy>
</dependencies>
```

For gradle:
```text
compile 'eu.tsystems.mms.tic.testerra:core:1-SNAPSHOT'
```

###### Parent module:
Or, for maven you can use the "parent" project, to have all modules at once:
```xml
<parent>
    <groupId>eu.tsystems.mms.tic.testerra</groupId>
    <artifactId>parent</artifactId>
    <version>1-SNAPSHOT</version>
</parent>
```

###### Using testerra functionality:

Create a Test Class and extend the TesterraTest class:

```java
public class MyTest extends TesterraTest {
    
    @Test
    public void testT01_My_first_tt._test() {
        // ...
    }
}
```

***

Documentation pending
