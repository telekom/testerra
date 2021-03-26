<h1 align="center">
    Testerra
</h1>

<p align="center">
    <a href="https://mvnrepository.com/artifact/io.testerra" title="MavenCentral"><img src="https://img.shields.io/maven-central/v/io.testerra/core?label=Maven%20Central"></a>
    <a href="/../../commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/telekom/testerra?style=flat"></a>
    <a href="/../../issues" title="Open Issues"><img src="https://img.shields.io/github/issues/telekom/testerra?style=flat"></a>
    <a href="./LICENSE" title="License"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg?style=flat"></a>
</p>

<p align="center">
  <a href="#installation">Installation</a> •
  <a href="#documentation">Documentation</a> •
  <a href="#support-and-feedback">Support</a> •
  <a href="#how-to-contribute">Contribute</a> •
  <a href="#contributors">Contributors</a> •
  <a href="#licensing">Licensing</a>
</p>

## About Testerra
Testerra is an integrated framework for automating tests for (web) applications. Testerra can also be understood as a building block for test automation projects with various basic components. It also includes a prepared "foundation" on which complex test automation environments can be built. Testerra is developed by our Test Automation Experts at T-Systems MMS GmbH Dresden (Website). In numerous projects Testerra is used as the standard test automation framework.

You may see Testerra as an open source test automation library for web frontend testing. It provides a tool suite for many use cases: a base API for Page Object Pattern (including responsive layouts) and GuiElements (smarter WebElements (Selenium)), enhanced reporting functionality, a utility collection and some additional helpful modules.

## Installation

_Comming soon:_ Testerra and its components are published at GitHub packages and Maven Central.

Maven:
````xml
<dependencies>
    <dependency>
        <groupId>io.testerra</groupId>
        <artifactId>driver-ui-desktop</artifactId>
        <version>2-SNAPSHOT</version>
    </dependecy>
    <dependency>
        <groupId>io.testerra</groupId>
        <artifactId>report-ng</artifactId>
        <version>2-SNAPSHOT</version>
    </dependecy>
</dependencies>
````

Gradle:
````groovy
compile 'io.testerra:driver-ui-desktop:2-SNAPSHOT'
compile 'io.testerra:report-ng:2-SNAPSHOT'
````

### Using testerra functionality:

Create a Test Class and extend the TesterraTest class:

````java
public class MyTest extends TesterraTest {
    
    @Test
    public void testT01_My_first_tt_test() {
        // ...
    }
}
````

Feel free to try out our ready-to-use Skeleton project: [testerra-skeleton].

## Documentation

Check out our comprehensive [Testerra documentation](http://docs.testerra.io/testerra/2.0-RC-3/)!

## Publishing

Testerra is deployed and published to Maven Central. All JAR files are signed via Gradle signing plugin.

The following properties have to be set via command line or ``~/.gradle/gradle.properties``

| Property                      | Description                                           |
| ----------------------------- | ----------------------------------------------------- |
| `ttVersion`                   | Version of deployed Testerra, default is `1-SNAPSHOT` |
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
| **Other Requests**    | <a href="mailto:testerra@t-systems-mms.com" title="Email us"><img src="https://img.shields.io/badge/email-CWA%20team-green?logo=mail.ru&style=flat-square&logoColor=white"></a>   |

## Additional repositories

| Repository          | Description                                                           |
| ------------------- | --------------------------------------------------------------------- |
| [testerra-skeleton] | Testerra Skeleton Project |
| [testerra-selenoid-connector] | Testerra Selenoid Connector |
| [testerra-hpqc-connector] | Testerra HPQC Connector |
| [testerra-teamcity-connector] | Testerra TeamCity Connector |
| [testerra-cucumber-connector] | Testerra Cucumber Connector |
| [testerra-xray-connector] | Testerra Xray Connector |
| [testerra-appium-connector] | Testerra Appium Connector |
| [testerra-azure-devops-connector] | Testerra Azure DevOps Connector |

[testerra]: https://github.com/telekom/testerra
[testerra-skeleton]: https://github.com/telekom/testerra-skeleton
[testerra-selenoid-connector]: https://github.com/telekom/testerra-selenoid-connector
[testerra-hpqc-connector]: https://github.com/telekom/testerra-hpqc-connector
[testerra-teamcity-connector]: https://github.com/telekom/testerra-teamcity-connector
[testerra-cucumber-connector]: https://github.com/telekom/testerra-cucumber-connector
[testerra-xray-connector]: https://github.com/telekom/testerra-xray-connector
[testerra-appium-connector]: https://github.com/telekom/testerra-appium-connector
[testerra-azure-devops-connector]: https://github.com/telekom/testerra-azure-devops-connector

## How to Contribute

Contribution and feedback is encouraged and always welcome. For more information about how to contribute, the project structure, as well as additional contribution information, see our [Contribution Guidelines](./CONTRIBUTING.md). By participating in this project, you agree to abide by its [Code of Conduct](./CODE_OF_CONDUCT.md) at all times.

## Contributors

At the same time our commitment to open source means that we are enabling -in fact encouraging- all interested parties to contribute and become part of its developer community.

## Licensing

Copyright (c) 2020 Deutsche Telekom AG.

Licensed under the **Apache License, Version 2.0** (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under the License.
