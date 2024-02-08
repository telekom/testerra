<h1 align="center">
    Testerra
</h1>



<p align="center">
    <a href="https://mvnrepository.com/artifact/io.testerra" title="MavenCentral"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.testerra/core/2?label=Maven%20Central"></a>
    <a href="/../../commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/telekom/testerra?style=flat"></a>
    <a href="/../../issues" title="Open Issues"><img src="https://img.shields.io/github/issues/telekom/testerra?style=flat"></a>
    <a href="./LICENSE" title="License"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg?style=flat"></a>
</p>

<p align="center">
  <a href="#setup">Setup</a> •
  <a href="#documentation">Documentation</a> •
  <a href="#extras">Extras</a> •
  <a href="#support-and-feedback">Support</a> •
  <a href="#how-to-contribute">Contribute</a> •
  <a href="#contributors">Contributors</a> •
  <a href="#licensing">Licensing</a>
</p>

## About Testerra

<p align="center">
    <picture>
        <source media="(prefers-color-scheme: light)" srcset="./docs/src/images/s_Testerra_Logo_0256px.png">
        <img alt="Text changing depending on mode. Light: 'So light!' Dark: 'So dark!'" src="./docs/src/images/w_Testerra_Logo_0256px.png">
    </picture>
</p>

It is an integrated Java framework for automating tests for (web) applications. Testerra can also be understood as a building block for test automation projects with various basic components.

You may see Testerra as an open source test automation library for web frontend testing. It provides a tool suite for many use cases: a base API for Page Object Pattern (including responsive layouts) and GuiElements (smarter WebElements (Selenium)), enhanced reporting functionality, a utility collection and some additional helpful modules.

Testerra is developed by our Test Automation Experts at Telekom MMS in Dresden. In numerous projects Testerra is used as the standard test automation framework and includes the experience of more then 10 years of test automation.

**This is the branch of Testerra 2. For Testerra 1 go to https://github.com/telekom/testerra/tree/testerra1** 

## Setup

Include the following dependency in your project.

Gradle:
```groovy
dependencies {
    implementation 'io.testerra:driver-ui-desktop:2.7'
    implementation 'io.testerra:report-ng:2.7'
}
```

Maven:
```xml
<dependencies>
    <dependency>
        <groupId>io.testerra</groupId>
        <artifactId>driver-ui-desktop</artifactId>
        <version>2.7</version>
    </dependency>
    <dependency>
        <groupId>io.testerra</groupId>
        <artifactId>report-ng</artifactId>
        <version>2.7</version>
    </dependency>
</dependencies>
```

Testerra requires **JDK11** or later and uses **Selenium 4**.

### Using Testerra functionality

Create a Test Class and extend the TesterraTest class:

````java
public class MyTest extends TesterraTest implements UiElementFinderFactoryProvider, WebDriverManagerProvider {
    
    @Test
    public void testT01_My_first_tt_test() {
        UiElementFinder finder = UI_ELEMENT_FINDER_FACTORY.create(WEB_DRIVER_MANAGER.getWebDriver());
        
        finder.find(By.name("q"))
                .type("Hello World")
                .expect().value().endsWith("Hello").is(false);
    }
}
````

## Documentation

* Check out our comprehensive [Testerra 2 documentation](https://docs.testerra.io/testerra/2-latest/index.html)!
* Feel free to try out our ready-to-use [Skeleton project][testerra-skeleton].
* Some more features can be found [in the Testerra demo project][testerra-demo].

## Extras

### Testing

Every module contains tests that can be run like
```shell
gradle test
```

The following optional properties can be set.

| Property                      | Description                                           |
| ----------------------------- | ----------------------------------------------------- |
| `withJacoco`                   | Enables Jacoco code coverage analysis |

### License report

Create a report about used licenses for every dependency:

``gradle generateLicenseReport``

You will find the reports of all modules under  ``license3rdparty``.

### Publishing

Testerra is deployed and published to Maven Central. All JAR files are signed via Gradle signing plugin.

The following properties have to be set via command line or ``~/.gradle/gradle.properties``

| Property                      | Description                                           |
| ----------------------------- | ----------------------------------------------------- |
| `ttVersion`                   | Version of deployed Testerra, default is `2-SNAPSHOT` |
| `deployUrl`                   | Maven repository URL                                  |
| `deployUsername`              | Maven repository username                             |
| `deployPassword`              | Maven repository password                             |
| `signing.keyId`               | GPG private key ID (short form)                       |
| `signing.password`            | GPG private key password                              |
| `signing.secretKeyRingFile`   | Path to GPG private key                               |

If all properties are set, call the following to build, deploy and release Testerra:
````shell
gradle buildReport publish closeAndReleaseRepository
````

## Code of Conduct

This project has adopted the [Contributor Covenant](https://www.contributor-covenant.org/) in version 2.0 as our code of conduct. Please see the details in our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md). All contributors must abide by the code of conduct.

## Working Language

We decided to apply _English_ as the primary project language.  

Consequently, all content will be made available primarily in English. We also ask all interested people to use English as language to create issues, in their code (comments, documentation etc.) and when you send requests to us. The application itself and all end-user faing content will be made available in other languages as needed.


## Support and Feedback
The following channels are available for discussions, feedback, and support requests:

| Type                     | Channel                                                |
| ------------------------ | ------------------------------------------------------ |
| **Issues**   | <a href="https://github.com/telekom/testerra/issues/new/choose" title="Issues"><img src="https://img.shields.io/github/issues/telekom/testerra?style=flat"></a> |
| **Other Requests**    | <a href="mailto:testerra@t-systems-mms.com" title="Email us"><img src="https://img.shields.io/badge/email-Testerra%20team-green?logo=mail.ru&style=flat-square&logoColor=white"></a>   |

## Additional repositories

### Testerra extensions

| Repository                        | Description                     |
|-----------------------------------|---------------------------------|
| [testerra-skeleton]               | Testerra Skeleton Project       |
| [testerra-demo]                   | Testerra demo projects          |
| [testerra-selenoid-connector]     | Testerra Selenoid Connector     |
| [testerra-hpqc-connector]         | Testerra HPQC Connector         |
| [testerra-teamcity-connector]     | Testerra TeamCity Connector     |
| [testerra-cucumber-connector]     | Testerra Cucumber Connector     |
| [testerra-xray-connector]         | Testerra Xray Connector         |
| [testerra-appium-connector]       | Testerra Appium Connector       |
| [testerra-azure-devops-connector] | Testerra Azure DevOps Connector |

[testerra]: https://github.com/telekom/testerra
[testerra-skeleton]: https://github.com/telekom/testerra-skeleton
[testerra-demo]: https://github.com/telekom-mms/testerra-demo
[testerra-selenoid-connector]: https://github.com/telekom/testerra-selenoid-connector
[testerra-hpqc-connector]: https://github.com/telekom/testerra-hpqc-connector
[testerra-teamcity-connector]: https://github.com/telekom/testerra-teamcity-connector
[testerra-cucumber-connector]: https://github.com/telekom/testerra-cucumber-connector
[testerra-xray-connector]: https://github.com/telekom/testerra-xray-connector
[testerra-appium-connector]: https://github.com/telekom/testerra-appium-connector
[testerra-azure-devops-connector]: https://github.com/telekom/testerra-azure-devops-connector

### Legacy Testerra modules

You will find outdated Testerra modules here: https://github.com/telekom/testerra-legacy

## How to Contribute

Contribution and feedback is encouraged and always welcome. For more information about how to contribute, the project structure, as well as additional contribution information, see our [Contribution Guidelines](./CONTRIBUTING.md). By participating in this project, you agree to abide by its [Code of Conduct](./CODE_OF_CONDUCT.md) at all times.

## Contributors

At the same time our commitment to open source means that we are enabling -in fact encouraging- all interested parties to contribute and become part of its developer community.

## Licensing

Copyright (c) 2020 Deutsche Telekom AG.

Licensed under the **Apache License, Version 2.0** (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under the License.
