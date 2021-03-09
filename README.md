<h1 align="center">
    Testerra
</h1>

<p align="center">
    <a href="https://github.com/telekom/testerra/commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/telekom/testerra?style=flat"></a>
    <a href="https://github.com/telekom/testerra/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/telekom/testerra?style=flat"></a>
    <a href="https://github.com/telekom/testerra/blob/master/LICENSE" title="License"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg?style=flat"></a>
</p>

<p align="center">
  <a href="#installation">Installation</a> •
  <a href="#documentation">Documentation</a> •
  <a href="#development">Development</a> •
  <a href="#support-and-feedback">Support</a> •
  <a href="#how-to-contribute">Contribute</a> •
  <a href="#contributors">Contributors</a> •
  <a href="#licensing">Licensing</a>
</p>

## About Testerra
Testerra is an integrated framework for automating tests for (web) applications. Testerra can also be understood as a building block for test automation projects with various basic components. It also includes a prepared "foundation" on which complex test automation environments can be built. Testerra is developed by our Test Automation Experts at T-Systems Multimedia Solutions GmbH Dresden (Website). In numerous projects Testerra is used as the standard test automation framework.

You may see Testerra as an open source test automation library for web frontend testing. It provides a tool suite for many use cases: a base API for Page Object Pattern (including responsive layouts) and GuiElements (smarter WebElements (Selenium)), enhanced reporting functionality, a utility collection and some additional helpful modules.

## Installation

_Comming soon:_ Testerra and its components are published on GitHub packages and Maven Central.

Maven:
````xml
<dependencies>
    <dependency>
        <groupId>eu.tsystems.mms.tic.testerra</groupId>
        <artifactId>driver-ui-desktop</artifactId>
        <version>1-SNAPSHOT</version>
    </dependecy>
</dependencies>
````

Gradle:
````text
compile 'eu.tsystems.mms.tic.testerra:driver-ui-desktop:1-SNAPSHOT'
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

## Documentation

Check out our comprehensive [Testerra documentation](https://tapas-docs.s3.eu-central-1.amazonaws.com/testerra/latest/index.html)!

## Development

## Code of Conduct

This project has adopted the [Contributor Covenant](https://www.contributor-covenant.org/) in version 2.0 as our code of conduct. Please see the details in our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md). All contributors must abide by the code of conduct.

## Working Language

We decided to apply _English_ as the primary project language.  

Consequently, all content will be made available primarily in English. We also ask all interested people to use English as language to create issues, in their code (comments, documentation etc.) and when you send requests to us. The application itself and all end-user faing content will be made available in other languages as needed.


## Support and Feedback
The following channels are available for discussions, feedback, and support requests:

| Type                     | Channel                                                |
| ------------------------ | ------------------------------------------------------ |
| **General Discussion**   | <a href="https://github.com/telekom/testerra/issues/new/choose" title="General Discussion"><img src="https://img.shields.io/github/issues/telekom/testerra/question.svg?style=flat-square"></a> </a>   |
| **Feedback**    | <a href="https://github.com/telekom/testerra/issues/new/choose" title="Open Concept Feedback"><img src="https://img.shields.io/github/issues/telekom/testerra/architecture.svg?style=flat-square"></a>  |
| **Testerra**    | <a href="https://github.com/telekom/testerra/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/telekom/testerra?style=flat"></a>  |
| **Other Requests**    | <a href="mailto:opensource@telekom.de" title="Email us"><img src="https://img.shields.io/badge/email-CWA%20team-green?logo=mail.ru&style=flat-square&logoColor=white"></a>   |

## How to Contribute

Contribution and feedback is encouraged and always welcome. For more information about how to contribute, the project structure, as well as additional contribution information, see our [Contribution Guidelines](./CONTRIBUTING.md). By participating in this project, you agree to abide by its [Code of Conduct](./CODE_OF_CONDUCT.md) at all times.

## Contributors

At the same time our commitment to open source means that we are enabling -in fact encouraging- all interested parties to contribute and become part of its developer community.

## Licensing

Copyright (c) 2020 Deutsche Telekom AG.

Licensed under the **Apache License, Version 2.0** (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under the License.
