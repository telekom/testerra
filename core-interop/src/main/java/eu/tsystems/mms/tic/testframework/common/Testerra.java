package eu.tsystems.mms.tic.testframework.common;

public class Testerra {
    public enum Properties {
        GUIELEMENT_DEFAULT_ASSERT_IS_COLLECTOR("tt.guielement.default.assertcollector", false);
        private final String property;
        private final Object defaultValue;

        Properties(String property, Object defaultValue) {
            this.property = property;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return property;
        }
        public Object defaultValue() {
            return defaultValue;
        }

        public Double asDouble() {
            return PropertyManager.getDoubleProperty(property, (Double) defaultValue);
        }

        public Long asLong() {
            return PropertyManager.getLongProperty(property, (Long)defaultValue);
        }

        public String asString() {
            return PropertyManager.getProperty(property, defaultValue.toString());
        }

        public Boolean asBool() {
            return PropertyManager.getBooleanProperty(property, (Boolean)defaultValue);
        }

    }
}
