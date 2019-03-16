package com.solacesystems.ubersol;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HelperTest {
    private static final String ipnoport = "11.12.13.14";
    private static final String ipandport = "11.12.13.14:98765";
    private static final String nohostandport = ":98765";

    @Test
    public void getPortTest() {
        int port = Helper.getPort(ipnoport);
        assertEquals(22, port);

        port = Helper.getPort(ipandport);
        assertEquals(98765, port);

        port = Helper.getPort(nohostandport);
        assertEquals(98765, port);
    }

    @Test
    public void getHostTest() {
        String host = Helper.getHost(ipnoport);
        assertEquals(host, ipnoport);

        host = Helper.getHost(ipandport);
        assertEquals("11.12.13.14", host);

        host = Helper.getHost(nohostandport);
        assertEquals("", host);
    }

    @Test
    public void regexToGlobTest() {
        String f = "foo*bar*";
        f = f.replaceAll("\\*", ".*");
        assertEquals("foo.*bar.*", f);
    }
}
