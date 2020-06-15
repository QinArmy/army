package com.example.simple;


import io.army.util.NetUtils;
import io.army.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        float f = Float.MAX_VALUE - 1F;
        int i = (int)f;
        System.out.println(i);
        System.out.println(Integer.MAX_VALUE);
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
    public void virtualMethod() throws Exception {
        Map<String, NetworkInterface> map = NetUtils.obtainNetworkInterfaces();
        List<String> nameList = new ArrayList<>(map.keySet());
        nameList.sort(null);
        LOG.info("list:{}", nameList);
    }



}
