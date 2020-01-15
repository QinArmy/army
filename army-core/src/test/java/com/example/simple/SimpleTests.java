package com.example.simple;

import io.army.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

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

    }

    @Test
    public void tem() {
        System.out.println(String.valueOf(Byte.MAX_VALUE).length());
        System.out.println(String.valueOf(Byte.MAX_VALUE << 2).length());
    }

    @Test
    public void maxValue() {
        System.out.println(~(-1L << 24));
    }

    @Test
    public void size() {
        float f = Float.NEGATIVE_INFINITY;
        System.out.println(f);
    }

    @Test
    public void virtualMethod(){
        System.out.println(String.class.getClassLoader());
        System.out.println(getClass().getClassLoader());
    }



}
