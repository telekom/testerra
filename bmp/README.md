# bmp
bmp is a small utility module for using the [browsermob-proxy](https://github.com/lightbody/browsermob-proxy) in 
an easy way. 

## Installation / Usage

At least you have to put the core module into your project dependencies:

For maven:

```xml
<dependencies>
    <dependency>
        <groupId>eu.tsystems.mms.tic.testerra</groupId>
        <artifactId>bmp</artifactId>
        <version>1-SNAPSHOT</version>
    </dependecy>
</dependencies>
```

For gradle:
```text
compile 'eu.tsystems.mms.tic.testerra:bmp:1-SNAPSHOT'
```

###### Using bmp module:

The following example shows how to create a new local proxy instance and capture traffic.

```java
public class MyTest extends TesterraTest {
    
    @Test
    public void testT01_My_first_tt._test() {
        HttpHost proxyHost = new HttpHost(PROXY_HOST, PROXY_PORT);
        UsernamePasswordCredentials credentials = null;
        ProxyServer proxyServer = new ProxyServer(9999, proxyHost, credentials);

        proxyServer.startCapture();
        //...
        Har har = proxyServer.stopCapture();
    }
}
```

Or, more easily, you can use the proxy manager:

```java
public class MyTest extends TesterraTest {
    
    @Test
    public void testT01_My_first_tt._test() {
        final ProxyServer proxyServer = BMProxyManager.getProxyServer();
        // ...
        BMProxyManager.shutDownProxyServer();
    }
}
```

You can have a standalone installation of the lightproxy on a machine, controlling this proxy via REST.
Simplified you can do this with the BmpRestClient. In here you can easily set
* Basic Auth 
* Any headers
* Host name mapping
* Start a serving proxy instance

***

Documentation pending