/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.core.playground.pageobjects;

import eu.tsystems.mms.tic.testframework.AbstractTest;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.pageobjects.WebTestPage;
import eu.tsystems.mms.tic.testframework.report.model.BrowserInformation;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Date;

/**
 * Created by clgr on 16.10.2014.
 */
public class LoadPageObjectTest extends AbstractTest {

    @DataProvider(name = "test1", parallel = true)
    public static Object[][] getNumbers() {
        return new Object[][] { { 1 }, { 2 }, { 3 },
                { 4 }, { 5 }, { 6 }, { 7 }, { 8 } /* ,{9},{10},{11}, {12}, {13}, {14} */};
    }

    @Test
    public void test_XETA() {
        Long start = System.currentTimeMillis();
        WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(PropertyManager.getProperty("fennec.baseurl"));
        WebElement wishList = driver.findElement(By.id("wishlist-total"));
        System.out.println("List: " + wishList.getText());
        WebElement loginLink = driver.findElement(By.xpath("//a[text()='login']"));
        loginLink.click();
        // TestUtils.sleep(5000);
        System.out.println("Title: " + driver.getTitle());

        WebElement mailField = driver.findElement(By.xpath("//input[@name='email']"));

        WebElement passwordField = driver.findElement(By.xpath("//input[@name='password']"));
        WebElement loginButton = driver.findElement(By.xpath("//input[@value='Login']"));

        mailField.sendKeys("test.user@example.org");
        System.out.println("Mail: " + mailField.getAttribute("value"));
        passwordField.sendKeys("Telekom01");
        System.out.println("PW: " + passwordField.getAttribute("value"));
        loginButton.click();
        // TestUtils.sleep(5000);
        System.out.println("Title: " + driver.getTitle());

        System.out.println("Duration: " + (System.currentTimeMillis() - start) + " ms");
    }

    @Test
    public void test_Hunit() throws Exception {
        Long start = System.currentTimeMillis();
        DesiredCapabilities desiredCapabilities = DesiredCapabilities.htmlUnit();
        // desiredCapabilities.setCapability("setThrowExceptionOnScriptError",false);
//        desiredCapabilities.setCapability(CapabilityType.VERSION, BrowserVersion.FIREFOX_24);
        // desiredCapabilities.setBrowserName("Mozilla/5.0 (X11; Linux x86_64; rv:24.0) Gecko/20100101 Firefox/24.0");
        // desiredCapabilities.setVersion("24.0");
        desiredCapabilities.setJavascriptEnabled(true);

        RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), desiredCapabilities);

        // HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_24);
        // driver.setJavascriptEnabled(true);
        driver.get("http://192.168.58.99/");
        String userAgent = (String) ((JavascriptExecutor)
                driver).executeScript("return navigator.userAgent;");
        final BrowserInformation brInformation = new BrowserInformation(userAgent);
        String browserInfo = brInformation.getBrowserName() + " - v" + brInformation.getBrowserVersion();
        System.out.println("Browser: " + browserInfo);
        WebElement wishList = driver.findElementById("wishlist-total");
        System.out.println("List: " + wishList.getText());
        WebElement loginLink = driver.findElementByXPath("//a[text()='login']");
        loginLink.click();
        // TestUtils.sleep(5000);
        System.out.println("Title: " + driver.getTitle());

        WebElement mailField = driver.findElementByXPath("//input[@name='email']");

        WebElement passwordField = driver.findElementByXPath("//input[@name='password']");
        WebElement loginButton = driver.findElementByXPath("//input[@value='Login']");

        mailField.sendKeys("test.user@example.org");
        System.out.println("Mail: " + mailField.getAttribute("value"));
        passwordField.sendKeys("Telekom01");
        System.out.println("PW: " + passwordField.getAttribute("value"));
        loginButton.click();
        // TestUtils.sleep(5000);
        System.out.println("Title: " + driver.getTitle());

        System.out.println("Duration: " + (System.currentTimeMillis() - start) + " ms");
        /*
         * WebTestPage fennecWebTestPage = new WebTestPage(); // fennecWebTestPage = fennecWebTestPage.search("bla");
         * // fennecWebTestPage = fennecWebTestPage.click();
         * 
         * fennecWebTestPage.assertFunctionalityOfButton1(); fennecWebTestPage = fennecWebTestPage.reloadPage(); /* WebClient
         * webClient = new WebClient(BrowserVersion.FIREFOX_24,"proxy.mms.dresden.de",8080);
         * 
         * webClient.getOptions().setJavaScriptEnabled(true);
         * webClient.getOptions().setThrowExceptionOnScriptError(false);
         * 
         * HtmlPage google = webClient.getPage("http://www.google.de"); HtmlInput searchBar =
         * google.getHtmlElementById("gbqfq"); searchBar.setValueAttribute("Pause");
         */

        /**
         * Google
         * 
         * // Find the text input element by its name WebElement element = driver.findElement(By.id("gbqfq"));
         * 
         * // Enter something to search for element.sendKeys("Cheese!");
         * 
         * // Now submit the form. WebDriver will find the form for us from the element element.submit();
         * 
         * // Check the title of the page System.out.println("Page title is: " + driver.getTitle());
         * 
         * driver.quit();
         */
    }

    @Test(dataProvider = "test1", threadPoolSize = 1, invocationCount = 2)
    public void testT01_SimplePageTest_Load(Integer nr) {
        // java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
        // System.out.println(i);
        // long startTime = System.currentTimeMillis();
        // WebDriverManager.setGlobalExtraCapability("version", "26");

        final WebDriver driver = WebDriverManager.getWebDriver();
        driver.get(WebTestPage.URL);
        WebTestPage fennecWebTestPage = new WebTestPage(driver);
        // fennecWebTestPage = fennecWebTestPage.search("bla");
        // fennecWebTestPage = fennecWebTestPage.click();

        fennecWebTestPage.assertFunctionalityOfButton1();
        fennecWebTestPage = fennecWebTestPage.reloadPage();
        // TestUtils.sleep(5000);
        fennecWebTestPage.reloadPage();
        System.out.println("Ende: " + new Date().toString());

        // long stopTime = System.currentTimeMillis();
        // long elapsedTime = stopTime - startTime;
        // String formatedTime = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(elapsedTime),
        // TimeUnit.MILLISECONDS.toSeconds(elapsedTime)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedTime)));
        // System.out.println(formatedTime);

    }
}
