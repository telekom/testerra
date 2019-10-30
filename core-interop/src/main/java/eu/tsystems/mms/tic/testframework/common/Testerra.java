package eu.tsystems.mms.tic.testframework.common;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class Testerra {

    private static Injector ioc;

    public enum Properties implements IProperties {
        DRY_RUN("tt.dryrun", false),
        MONITOR_MEMORY("tt.monitor.memory", true),
        ;
        private final String property;
        private Object defaultValue;

        Properties(String property, Object defaultValue) {
            this.property = property;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return property;
        }
        public IProperties useDefault(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @Override
        public Double asDouble() {
            return PropertyManager.getDoubleProperty(property, (Double) defaultValue);
        }
        @Override
        public Long asLong() { return PropertyManager.getLongProperty(property, (Long)defaultValue); }
        @Override
        public Boolean asBool() { return PropertyManager.getBooleanProperty(property, (Boolean)defaultValue); }

        @Override
        public String asString() {
            return null;
        }
    }

    /**
     * We initialize the IoC modules in a reverse sorted class name order,
     * to be able to override module configures.
     * This is not best practice, bad currently the only way to override module bindings.
     * Because {@link Modules#override(Module...)} doesn't work in the way we need it.
     */
    public static Injector ioc() {
        if (ioc==null) {
            final Reflections reflections = new Reflections(TesterraCommons.DEFAULT_PACKAGE_NAME);
            final Set<Class<? extends AbstractModule>> classes = reflections.getSubTypesOf(AbstractModule.class);
            final Iterator<Class<? extends AbstractModule>> iterator = classes.iterator();
            final TreeMap<String, Module> sortedModules = new TreeMap<>();
            try {
                while (iterator.hasNext()) {
                    final Class<? extends AbstractModule> moduleClass = iterator.next();
                    final Constructor<?> ctor = moduleClass.getConstructor();
                    sortedModules.put(moduleClass.getSimpleName(), (Module) ctor.newInstance());
                }
                final List<Module> reverseSortedModules = new ArrayList<>(sortedModules.values());
                Collections.reverse(reverseSortedModules);
                System.out.println(String.format("%s - Register IoC modules: %s", Testerra.class.getCanonicalName(), reverseSortedModules));
                ioc = Guice.createInjector(reverseSortedModules);
            } catch (Exception e) {
                e.printStackTrace();
                //LOGGER.error("Unable to initialize IoC modules", e);
            }
        }
        return ioc;
    }
}
