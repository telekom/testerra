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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.core.test.common;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by pele on 28.05.2014.
 */
public class PropertyManagerTest extends AbstractWebDriverTest {

    /**
     * property test with system properties and replacement system
     */
    @Test
    public void testT01a_ReplaceSystemProperty() {
        System.setProperty("myprop", "bla_{huhu}");
        System.setProperty("huhu", "blubb");
        String property = PropertyManager.getProperty("myprop", "nothing");

        Assert.assertEquals(property, "bla_blubb", "SystemProperty Replacement works");
    }

    @Test
    public void testT01b_ReplaceSystemProperty_SensibleData() {
        System.setProperty("myprop", "@SENSIBLE@bla_{huhu}");
        System.setProperty("huhu", "@SENSIBLE@blubb");
        String property = PropertyManager.getProperty("myprop", "nothing");

        Assert.assertEquals(property, "bla_blubb", "SystemProperty Replacement works");
    }

    /**
     * property test with boolean from property manager
     */
    @Test
    public void testT02BooleanFileTrue() {
        boolean prop = PropertyManager.getBooleanProperty("pm.test.boolean1", false);
        Assert.assertTrue(prop, "Boolean property from file");
    }

    /**
     * property test with boolean from property manager
     */
    @Test
    public void testT03BooleanFileFalse() {
        boolean prop = PropertyManager.getBooleanProperty("pm.test.boolean2", true);
        Assert.assertFalse(prop, "Boolean property from file");
    }

    /**
     * property test with default boolean
     */
    @Test
    public void testT04BooleanDefault() {
        boolean prop = PropertyManager.getBooleanProperty("pm.test.boolean3", false);
        Assert.assertFalse(prop, "Boolean property default");
    }

    /**
     * property test with boolean from system properties
     */
    @Test
    public void testT05SystBoolean1() {
        System.setProperty("pm.test.boolean4", "true");
        boolean booleanProperty = PropertyManager.getBooleanProperty("pm.test.boolean4", true);
        Assert.assertTrue(booleanProperty, "Boolean property from system");
    }

    /**
     * property test with boolean from system properties
     */
    @Test
    public void testT06SystBoolean2() {
        System.setProperty("pm.test.boolean5", "false");
        boolean booleanProperty = PropertyManager.getBooleanProperty("pm.test.boolean5", false);
        Assert.assertFalse(booleanProperty, "Boolean property from system");
    }

    /**
     * property test with boolean from system properties
     */
    @Test
    public void testT07SystBoolean3() {
        System.setProperty("pm.test.boolean6", "false");
        boolean booleanProperty = PropertyManager.getBooleanProperty("pm.test.boolean6", true);
        Assert.assertFalse(booleanProperty, "Boolean property from system");
    }

    /**
     * property test with boolean from system properties with replacement system
     */
    @Test
    public void testT08BooleanEinsetzen1() {
        System.setProperty("pm.test.boolean7", "false");
        System.setProperty("pm.test.boolean8", "{pm.test.boolean7}");
        boolean booleanProperty = PropertyManager.getBooleanProperty("pm.test.boolean8", true);
        Assert.assertFalse(booleanProperty, "Boolean property replacement");
    }

    /**
     * property test with integer from property manager
     */
    @Test
    public void testT09IntFile1() {
        int intProperty = PropertyManager.getIntProperty("pm.test.int1");
        Assert.assertEquals(intProperty, 0, "Int property from file");
    }

    /**
     * property test with integer from property manager
     */
    @Test
    public void testT10IntFile2() {
        int intProperty = PropertyManager.getIntProperty("pm.test.int2");
        Assert.assertEquals(intProperty, 465, "Int property from file");
    }

    /**
     * property test with default integer
     */
    @Test
    public void testT11IntDefault() {
        int intProperty = PropertyManager.getIntProperty("pm.test.int3", 1966);
        Assert.assertEquals(intProperty, 1966, "Int property default");
    }

    /**
     * property test with integer from system properties
     */
    @Test
    public void testT12SIntSystem1() {
        System.setProperty("pm.test.int4", "1324");
        int intProperty = PropertyManager.getIntProperty("pm.test.int4", 1324);
        Assert.assertEquals(intProperty, 1324, "Int property from system");
    }

    /**
     * property test with integer from system properties
     */
    @Test
    public void testT13IntSystem2() {
        System.setProperty("pm.test.int5", "13498765");
        int intProperty = PropertyManager.getIntProperty("pm.test.int5", 13498765);
        Assert.assertEquals(intProperty, 13498765, "Int property from system");
    }

    /**
     * property test with integer from system properties with replacement system
     */
    @Test
    public void testT14IntEinsetzen() {
        System.setProperty("pm.test.int5", "500");
        System.setProperty("pm.test.int6", "3{pm.test.int5}");
        int intProperty = PropertyManager.getIntProperty("pm.test.int6");
        Assert.assertEquals(intProperty, 3500, "Int property replacement");
    }

    /**
     * property test with string files from system properties
     */
    @Test
    public void testT15StringSystem1() {
        System.setProperty("pm.test.str1", "B");
        String property = PropertyManager.getProperty("pm.test.str1");
        Assert.assertEquals(property, "B", "property from system");
    }

    /**
     * property test with string files from system properties
     */
    @Test
    public void testT16StrSystem1() {
        System.setProperty("pm.test.str2", "Haus");
        String property = PropertyManager.getProperty("pm.test.str2");

        Assert.assertEquals(property, "Haus", "property from system");
    }

    /**
     * property test with string files from system properties with replacement system
     */
    @Test
    public void testT17StrEinsetzen1() {
        System.setProperty("pm.test.str3", "Peter_{Nachname}");
        System.setProperty("Nachname", "Pan");
        String property = PropertyManager.getProperty("pm.test.str3");
        Assert.assertEquals(property, "Peter_Pan", "property system replacement");
    }

    /**
     * property test with string files from system properties with replacement system
     */
    @Test
    public void testt18Streinsetzen2() {
        System.setProperty("pm.test.str4", "League_{1}_{pm.test.str5}");
        System.setProperty("1", "of");
        System.setProperty("pm.test.str5", "Legends");
        String property = PropertyManager.getProperty("pm.test.str4");
        Assert.assertEquals(property, "League_of_Legends", "property system replacement ");
    }

    /**
     * property test with string file from property manager
     */
    @Test
    public void testt19Stringfile1() {
        String property = PropertyManager.getProperty("pm.test.str6");
        Assert.assertEquals(property, "A", "property from file");
    }

    /**
     * property test with string file from property manager
     */
    @Test
    public void testt20Stringfile2() {
        String property = PropertyManager.getProperty("pm.test.str7");
        Assert.assertEquals(property, "Student", "property from file");
    }

    /**
     * property test with default string file
     */
    @Test
    public void testt21Stringfiledefault() {
        String property = PropertyManager.getProperty("pm.test.str8", "Test");
        Assert.assertEquals(property, "Test", "property default");
    }

    @Test
    public void testT31_MultiReplace() {
        System.setProperty("myprop", "test_{prop1}_{prop5}");
        System.setProperty("prop1", "value1");
        System.setProperty("prop2", "value2");
        System.setProperty("prop3", "value3_{prop2}");
        System.setProperty("prop4", "value4_{prop3}");
        System.setProperty("prop5", "value5_{prop4}_{prop1}");
        String property = PropertyManager.getProperty("myprop", "nothing");

        Assert.assertEquals(property, "test_value1_value5_value4_value3_value2_value1", "SystemProperty Replacement works");
    }

    @Test
    public void testT32_MultiReplace_Sensible() {
        System.setProperty("myprop", "test_{prop1}_{prop5}");
        System.setProperty("prop1", "value1");
        System.setProperty("prop2", "value2");
        System.setProperty("prop3", "@SENSIBLE@value3_{prop2}");
        System.setProperty("prop4", "value4_{prop3}");
        System.setProperty("prop5", "value5_{prop4}_{prop1}");
        String property = PropertyManager.getProperty("myprop", "nothing");

        Assert.assertEquals(property, "test_value1_value5_value4_value3_value2_value1", "SystemProperty Replacement works");
    }
}
