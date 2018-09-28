# jfennec mail-connector
jfennec mail-connector provides a simplified tool set for connections to mail boxes.

## Installation / Usage

For maven:

```xml
<dependencies>
    <dependency>
        <groupId>eu.tsystems.mms.tic.jfennec</groupId>
        <artifactId>mail-connector</artifactId>
        <version>1-SNAPSHOT</version>
    </dependecy>
</dependencies>
```

For gradle:
```text
compile 'eu.tsystems.mms.tic.jfennec:mail-connector:1-SNAPSHOT'
```

###### Using mail-connector module:

The following example shows how to create a connector and wait for mails:

```java
public class MyTest extends FennecTest {
    
    @Test
    public void testT01_My_first_fennec_test() {
        // create a new imap connector
        ImapConnector con = new ImapConnector(...);
        
        // wait for mails
        List<FennecMail> mails = con.waitForFennecMails(...);
    }
}
```

***

Documentation pending