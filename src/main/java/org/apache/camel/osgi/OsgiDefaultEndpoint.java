package org.apache.camel.osgi;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;


public class OsgiDefaultEndpoint extends DefaultEndpoint {

    private final BundleContext bundleContext;
    private final ClassLoader bundleClassLoader;

    private Map<String, String> props = Collections.emptyMap();

    public OsgiDefaultEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
        this.bundleClassLoader = getClass().getClassLoader();

        ClassLoader appClassLoader = component.getCamelContext().getApplicationContextClassLoader();

        Bundle bundle;
        if(!(appClassLoader instanceof BundleReference)) {
            // try to resolve classloader through reflection if BundleReference has already been wrapped in the custom classloader
            // currently it works with spring-dm, aries, eclipse genimi, camel
            try {
                Method method = appClassLoader.getClass().getMethod("getBundle");
                bundle = (Bundle) method.invoke(appClassLoader);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        String.format("ClassLoader of CamelContext [%s] is not OSGi aware", appClassLoader));
            }
        } else {
            bundle = BundleReference.class.cast(appClassLoader).getBundle();
        }

        bundleContext = bundle.getBundleContext();
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new OsgiDefaultConsumer(this, processor, getProps());
    }

    @Override
    public Producer createProducer() throws Exception {
        return new OsgiDefaultProducer(this, getProps());
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public OsgiComponent getComponent() {
        return (OsgiComponent) super.getComponent();
    }

    public Map<String, String> getProps() {
        return props;
    }

    public void setProps(Map<String, String> props) {
        this.props = props;
    }

    protected BundleContext getBundleContext() {
        return bundleContext;
    }

    protected ClassLoader getBundleClassLoader() {
        return bundleClassLoader;
    }
}
