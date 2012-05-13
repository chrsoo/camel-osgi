package org.apache.camel.osgi;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.osgi.framework.Bundle;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OsgiEndpointTypeTest {

    @Test
    public void testGetType() throws Exception {
        OsgiEndpointType endpointType = OsgiEndpointType.fromPath("");
        assertThat(endpointType, sameInstance(OsgiEndpointType.DEFAULT));

        endpointType = OsgiEndpointType.fromPath("default:test");
        assertThat(endpointType, sameInstance(OsgiEndpointType.DEFAULT));

        endpointType = OsgiEndpointType.fromPath("unknown:test");
        assertThat(endpointType, sameInstance(OsgiEndpointType.DEFAULT));

        endpointType = OsgiEndpointType.fromPath("multicast:test");
        assertThat(endpointType, sameInstance(OsgiEndpointType.MULTICAST));

        endpointType = OsgiEndpointType.fromPath("roundrobin:test");
        assertThat(endpointType, sameInstance(OsgiEndpointType.ROUNDROBIN));
    }

    @Test
    public void testGetName() throws Exception {
        assertThat(OsgiEndpointType.DEFAULT.getName("test"), equalTo("test"));
        assertThat(OsgiEndpointType.DEFAULT.getName("default:test"), equalTo("test"));
        assertThat(OsgiEndpointType.MULTICAST.getName("multicast:test"), equalTo("test"));
        assertThat(OsgiEndpointType.ROUNDROBIN.getName("roundrobin:test"), equalTo("test"));
    }

    @Test
    public void testCreateEndpoint() throws Exception {
        Bundle bundle = mock(Bundle.class);
        BundleDelegatingClassLoader classLoader = new BundleDelegatingClassLoader(bundle, getClass().getClassLoader());

        CamelContext camelContext = mock(CamelContext.class);
        when(camelContext.getApplicationContextClassLoader()).thenReturn(classLoader);

        OsgiComponent comp = new OsgiComponent();
        comp.setCamelContext(camelContext);

        assertThat(OsgiEndpointType.DEFAULT.createEndpoint("osgi:test", comp), instanceOf(OsgiDefaultEndpoint.class));
        assertThat(OsgiEndpointType.DEFAULT.createEndpoint("osgi:default:test", comp), instanceOf(OsgiDefaultEndpoint.class));
        assertThat(OsgiEndpointType.DEFAULT.createEndpoint("osgi:unknown:test", comp), instanceOf(OsgiDefaultEndpoint.class));
        assertThat(OsgiEndpointType.MULTICAST.createEndpoint("osgi:multicast:test", comp), instanceOf(OsgiMulticastEndpoint.class));
        assertThat(OsgiEndpointType.ROUNDROBIN.createEndpoint("osgi:roundrobin:test", comp), instanceOf(OsgiRoundRobinEndpoint.class));
    }

}
