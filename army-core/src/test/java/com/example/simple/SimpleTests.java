package com.example.simple;

import io.army.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * created  on 2018/11/18.
 */
public class SimpleTests {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleTests.class);

    @Test
    public void test() {
        LOG.info("out:{}", StringUtils.camelToUpperCase("userName"));
    }


    @Test
    public void testEnum() {
        Herb[] garden = {new Herb("a", Herb.Type.ANNUAL)};
        Set<Herb> herbSet = new HashSet<>();
        for (int i = 0; i < garden.length; i++) {

        }

    }

    @Test
    public void tem() {

    }


}
