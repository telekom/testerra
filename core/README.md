# jfennec core
jfennec core provides the framework core functionality: test control based on [testng](https://testng.org/doc/index.html), 
logging, reporting.  

## Installation / Usage

For maven:

```xml
<dependencies>
    <dependency>
        <groupId>eu.tsystems.mms.tic.jfennec</groupId>
        <artifactId>core</artifactId>
        <version>1-SNAPSHOT</version>
    </dependecy>
</dependencies>
```

For gradle:
```text
compile 'eu.tsystems.mms.tic.jfennec:core:1-SNAPSHOT'
```

###### Using console-connector module:

The main class to use is FennecTest. With all your test classes inheriting from this class, jfennec is activated
for every test you are executing.  

```java
public class MyTest extends FennecTest {
    
    @Test
    public void testT01_My_first_fennec_test() {
        //...
    }
}
```

Any module you are using will automatically hook into the execution by implementing the FennecHook:

```java
public class YourModuleHook implements ModuleHook {

    @Override
    public void init() {
        /*
        hook your module...
         */
        //start
        FennecListener.registerBeforeMethodWorker(...);

        //finish
        FennecListener.registerAfterMethodWorker(...);

        //shutdown
        FennecListener.registerGenerateReportsWorker(...);

        /*
        register services
         */
        // RetryAnalyzer
        RetryAnalyzer.registerAdditionalRetryAnalyzer(...);
        // Screenshots and Videos
        CollectAssertionInfoArtefacts.registerScreenshotCollector(...);
        CollectAssertionInfoArtefacts.registerVideoCollector(...);
        CollectAssertionInfoArtefacts.registerSourceCollector(...);

    }

    @Override
    public void terminate() {
        /*
         * tear down your module...
         */
        
        //...
    }
}
```

## FennecListener

The FennecListener is the core testng listener by which jfennec is controlling the execution. It is not recommended to 
overwrite this class, since you have the possibility to hook in your functionality. 

***

Documentation pending