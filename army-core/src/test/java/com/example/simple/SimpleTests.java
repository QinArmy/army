package com.example.simple;

import io.army.meta.TableMeta;
import io.army.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
        SortedSet<String> set = new TreeSet<>();
        set.add(TableMeta.ID);
        set.add(TableMeta.CREATE_TIME);
        set.add(TableMeta.VISIBLE);
        set.add(TableMeta.UPDATE_TIME);
        set.add(TableMeta.VERSION);

        LOG.info("set:{}", set);

    }


}
