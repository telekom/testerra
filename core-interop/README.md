# jfennec core-interop
jfennec core-interop is an abstraction of the core to provide core module access by interfaces without the
need of a direct coupling to the core module. This module is intended to be used by developers of additional
framework modules.

## Installation / Usage

For maven:

```xml
<dependencies>
    <dependency>
        <groupId>eu.tsystems.mms.tic.jfennec</groupId>
        <artifactId>core-interop</artifactId>
        <version>1-SNAPSHOT</version>
    </dependecy>
</dependencies>
```

For gradle:
```text
compile 'eu.tsystems.mms.tic.jfennec:core-interop:1-SNAPSHOT'
```

###### Using core-interop module:

The modules contains some core legs, with less functionality, like
* Flags
* Exceptions
* Constants
* Events
* PropertyManager
* ModuleHook and Worker Interfaces
* Reporting Interfaces
* Utils

***

Documentation pending