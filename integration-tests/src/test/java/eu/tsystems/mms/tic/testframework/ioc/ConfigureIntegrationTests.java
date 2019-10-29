//package eu.tsystems.mms.tic.testframework.ioc;
//
//import com.google.inject.Scopes;
//import eu.tsystems.mms.tic.testframework.internal.AssertionsCollector;
//import eu.tsystems.mms.tic.testframework.internal.EmptyAssertionsCollector;
//
//public class ConfigureIntegrationTests extends ConfigureCore {
//    @Override
//    protected void configure() {
//        configureAssertionsCollector();
//        super.configure();
//    }
//
//    @Override
//    protected void configureAssertionsCollector() {
//        bind(AssertionsCollector.class).to(EmptyAssertionsCollector.class).in(Scopes.SINGLETON);
//        assertionsCollectorConfigured = true;
//    }
//}
