# jfennec console-connector
jfennec console-connector provides SSH and Telnet connectors in a simplified way. 

## Installation / Usage

At least you have to put the core module into your project dependencies:

For maven:

```xml
<dependencies>
    <dependency>
        <groupId>eu.tsystems.mms.tic.jfennec</groupId>
        <artifactId>console-connector</artifactId>
        <version>1-SNAPSHOT</version>
    </dependecy>
</dependencies>
```

For gradle:
```text
compile 'eu.tsystems.mms.tic.jfennec:console-connector:1-SNAPSHOT'
```

###### Using console-connector module:

The following example shows how to create an SSH connection and execute a command.

```java
public class MyTest extends FennecTest {
    
    @Test
    public void testT01_My_first_fennec_test() {
        SSHConnector ssh = new SSHConnector(host, username, password);
        String output = ssh.execute("ls -al");
        //...
    }
}
```

***

Documentation pending