# core
core provides the framework core functionality: test control based on [testng](https://testng.org/doc/index.html), 
logging, reporting.  

## Installation / Usage

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

###### Using console-connector module:

The main class to use is TesterraTest. With all your test classes inheriting from this class, testerra is activated
for every test you are executing.  

```java
public class MyTest extends TesterraTest {
    
    @Test
    public void testT01_My_first_tt._test() {
        //...
    }
}
```

Any module you are using will automatically hook into the execution by implementing the TesterraHook:

```java
public class YourModuleHook implements ModuleHook {

    @Override
    public void init() {
        /*
        hook your module...
         */
        //start
        TesterraListener.registerBeforeMethodWorker(...);

        //finish
        TesterraListener.registerAfterMethodWorker(...);

        //shutdown
        TesterraListener.registerGenerateReportsWorker(...);

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

## TesterraListener

The TesterraListener is the core testng listener by which testerra is controlling the execution. It is not recommended to 
overwrite this class, since you have the possibility to hook in your functionality. 

***

Documentation pending