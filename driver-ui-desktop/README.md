# jfennec driver-ui-desktop
jfennec driver-ui-desktop is the desktop implementation for the ui-driver. It will register a selenium backed 
driver background for WebDriverManager, Page and GuiElement actions. Putting this module in your classpath with
automatically register firefox, chrome and more desktop browsers to the ui-driver to execute tests on.   

## Installation / Usage

For maven:

```xml
<dependencies>
    <dependency>
        <groupId>eu.tsystems.mms.tic.jfennec</groupId>
        <artifactId>driver-ui-desktop</artifactId>
        <version>1-SNAPSHOT</version>
    </dependecy>
</dependencies>
```

For gradle:
```text
compile 'eu.tsystems.mms.tic.jfennec:driver-ui-desktop:1-SNAPSHOT'
```

###### Using driver-ui-desktop module:

You won't do anything. By putting this module in your classpath, it's all done.

***

Documentation pending