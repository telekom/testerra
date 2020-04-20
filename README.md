# Testerra
Testerra is an open source test automation library for web frontend testing. It provides a tool suite for many use cases: 
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

Check out our comprehensive [Testerra documentation](https://tapas-docs.s3.eu-central-1.amazonaws.com/testerra/latest/index.html)!

At least you have to put the core module into your project dependencies:

For maven:

```xml
<dependencies>
    <dependency>
        <groupId>eu.tsystems.mms.tic.testerra</groupId>
        <artifactId>driver-ui-desktop</artifactId>
        <version>1-SNAPSHOT</version>
    </dependecy>
</dependencies>
```

For gradle:
```text
compile 'eu.tsystems.mms.tic.testerra:driver-ui-desktop:1-SNAPSHOT'
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
## Publishing

### ... to local Maven repo

Create a `gradle.properties` file with the following content.
```properties
systemProp.deployUrl=https://example.com
systemProp.deployUsername=user
systemProp.deployPassword=password
```
and run
```shell script
gradle publish
```
or pass then properties via. CLI
```shell script
gradle publish -DdeployUrl=https://example.com -DdeployUsername=user -DdeployPassword=password
```

### ... to Bintray

_Preparation_

* Folder jcenter contains basic Bintray publish files
* For publishing a module, add the following line to the module's ``build.gradle``
  ````
  // ...
  apply from: rootProject.file('release-bintray.gradle')
  ````
* Create/update the ``gradle.properties`` of the module (here: ``core`` module)
  ````
  POM_NAME=core
  POM_DESCRIPTION=Testerra Core modul
  POM_BINTRAY_NAME=eu.tsystems.mms.tic.testerra:core
  POM_ARTIFACT_ID=core
  POM_PACKAGING=jar
  ````

_Configuration_

All basic publish settings are located in ``release-bintray.gradle``

_Upload and publish_

* Before publishing, please note for Testerra version. You can only upload every version once!
* Publish one module (but not recommended)
  ````
  gradle gradle core:bintrayUpload
  ````
* Publish Testerra Framework
  ````
  gradle 
  ````

## Contributing
Thank you for considering contributing to the Testerra framework! The contribution guide can be found here: [CONTRIBUTING.md](CONTRIBUTING.md).

## License
The Testerra framework is open-sourced software licensed under the [Apache License Version 2.0](LICENSE).

