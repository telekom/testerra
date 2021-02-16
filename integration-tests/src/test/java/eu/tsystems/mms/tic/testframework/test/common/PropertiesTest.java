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
package eu.tsystems.mms.tic.testframework.test.common;

import eu.tsystems.mms.tic.testframework.common.IProperties;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PropertiesTest extends TesterraTest {
    enum Properties implements IProperties {
        INT("", 42),
        INT_STRING("", "42"),
        FLOAT("", 42.1337),
        FLOAT_STRING("", "42.1337"),
        BOOL_FALSE("", false),
        BOOL_TRUE("", true),
        BOOL_TRUE_STRING("", "true"),
        BOOL_FALSE_STRING("", " false "),
        STRING("", "katze")
        ;

        private final String property;
        private Object defaultValue;

        Properties(String property, Object defaultValue) {
            this.property = property;
            this.defaultValue = defaultValue;
        }

        @Override
        public IProperties newDefault(Object defaultValue) {
            return null;
        }

        @Override
        public Double asDouble() {
            return PropertyManager.getPropertiesParser().getDoubleProperty(property, defaultValue);
        }

        @Override
        public Long asLong() {
            return PropertyManager.getPropertiesParser().getLongProperty(property, defaultValue);
        }

        @Override
        public Boolean asBool() {
            return PropertyManager.getPropertiesParser().getBooleanProperty(property, defaultValue);
        }

        @Override
        public String asString() {
            return PropertyManager.getPropertiesParser().getProperty(property, defaultValue);
        }
    }

    @Test
    public void test_asBool() {
        Assert.assertEquals(Properties.BOOL_FALSE.asBool(), Boolean.FALSE);
        Assert.assertEquals(Properties.BOOL_TRUE.asBool(), Boolean.TRUE);
        Assert.assertEquals(Properties.BOOL_FALSE_STRING.asBool(), Boolean.FALSE);
        Assert.assertEquals(Properties.BOOL_TRUE_STRING.asBool(), Boolean.TRUE);
    }

    @Test
    public void test_asDouble() {
        Assert.assertEquals(Properties.FLOAT.asDouble().doubleValue(), 42.1337);
        Assert.assertEquals(Properties.FLOAT_STRING.asDouble(), Properties.FLOAT.asDouble());
    }

    @Test
    public void test_asLong() {
        Assert.assertEquals(Properties.INT.asLong().longValue(), 42L);
        Assert.assertEquals(Properties.INT_STRING.asLong().longValue(), 42L);
    }

    @Test
    public void test_asString() {
        Assert.assertEquals(Properties.INT.asString(), "42");
        Assert.assertEquals(Properties.FLOAT.asString(), "42.1337");
        Assert.assertEquals(Properties.STRING.asString(), "katze");
    }
}
