# Testerra
Testerra is an open source test automation library for web frontend testing. It provides a tool suite for many use cases: 
a base API for Page Object Pattern (including responsive layouts) and GuiElements (smarter WebElements (Selenium)), 
enhanced reporting functionality, a utility collection and some additional helpful modules.

## Installation / Usage

Check out our comprehensive [Testerra documentation](http://docs.testerra.io)!

At least you have to put the core module into your project dependencies:

For maven:

```xml
<dependencies>
    <dependency>
        <groupId>eu.tsystems.mms.tic.testerra</groupId>
        <artifactId>driver-ui-desktop</artifactId>
        <version>1-SNAPSHOT</version>
    </dependecy>
    <dependency>
        <groupId>eu.tsystems.mms.tic.testerra</groupId>
        <artifactId>report-ng</artifactId>
        <version>1-SNAPSHOT</version>
    </dependecy>
</dependencies>
```

For gradle:
```text
compile 'eu.tsystems.mms.tic.testerra:driver-ui-desktop:1-SNAPSHOT'
compile 'eu.tsystems.mms.tic.testerra:report-ng:1-SNAPSHOT'
```

## Using testerra functionality:

Create a Test Class and extend the TesterraTest class:

```java
public class MyTest extends TesterraTest {
    
    @Test
    public void testT01_My_first_tt_test() {
        // ...
    }
}
```
## Some internals

### Main modules

Framework core:
* [core](core/README.md)
* ([core-interop](core-interop/README.md))

UI Testing modules:
* [driver-ui](driver-ui/README.md)
* [driver-ui-desktop](driver-ui-desktop/README.md)

Connectors:
* [mail-connector](mail-connector/README.md)

Additional modules:
* [bmp](bmp/README.md)

### Publishing

#### Build report-ng

The `report-ng` module needs to build separately **before** publishing

```shell
gradle buildReport
```

#### Publish to a Maven repo

_Preparation_

* All publish settings are located in ``publish.gradle``.
* All modules will be published.
* To prevent publishing, add the following line to the ``build.gradle`` of the module:
  ```groovy
  doNotPublish(this)
  ```
  
_Publishing to local repo_

```shell
gradle publishToMavenLocal
```

_Publishing to remote repo_

```shell
gradle publish -DdeployUrl=<repo-url> -DdeployUsername=<repo-user> -DdeployPassword=<repo-password>
```

_Set a custom version_
```shell
gradle publish -DttVersion=<version>
```

#### Publish to GitHub

Some hints for using GitHub Packages as Maven repository

* Deploy URL is https://maven.pkg.github.com/OWNER/REPOSITRY
* As password generate an access token and grant permissions to ``write:packages`` (Settings -> Developer settings -> Personal access token)

### Cleaning

```shell
gradle clean cleanReports cleanNpm
```
  
## Contributing
Thank you for considering contributing to the Testerra framework! The contribution guide can be found here: [CONTRIBUTING.md](CONTRIBUTING.md).

## License
The Testerra framework is open-sourced software licensed under the [Apache License Version 2.0](LICENSE).

