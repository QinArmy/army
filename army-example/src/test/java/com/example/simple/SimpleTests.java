package com.example.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.net.InetAddress;

public class SimpleTests {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleTests.class);


    @Test
    public void annotation() {

    }

    @Test
    public void domainMeta() {

    }

    @Test
    public void mysqlDDL() throws Exception {
        long start = System.currentTimeMillis();
        LOG.info(InetAddress.getLocalHost().getHostName());
        LOG.info("cost {} ms", System.currentTimeMillis() - start);
    }
}
