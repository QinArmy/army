package io.army.mapping.optional;

import io.army.dialect._Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * <p>
 * This class unit test class of {@link PostgreArrays}.
 * </p>
 *
 * @see PostgreArrays
 */
@Test
public class PostgreArraysUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreArraysUnitTests.class);


    /**
     * @see PostgreArrays#parseArrayLength(String, int, int)
     */
    @Test
    public void arrayLength() {
        String text;
        int arrayLength;

        text = "{}";
        arrayLength = PostgreArrays.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 0);

        text = "{1}";
        arrayLength = PostgreArrays.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 1);

        text = "{ 1 ,2}";
        arrayLength = PostgreArrays.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 2);

        text = "{{ 1 ,2}}";
        arrayLength = PostgreArrays.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 1);

        text = "{{ 1 ,2} , {3,4}}";
        arrayLength = PostgreArrays.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 2);


        text = "{\"\"}";
        arrayLength = PostgreArrays.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 1);

        text = "{\"\"\"\"}";
        arrayLength = PostgreArrays.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 1);

        text = "{\"\\\"\"}";
        arrayLength = PostgreArrays.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 1);

        text = "{{\"meeting\", \"lunch\"}, {\"training\", \"presentation\"}}";
        arrayLength = PostgreArrays.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 2);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void arrayLengthNoStart() {
        String text;
        text = "1}";
        PostgreArrays.parseArrayLength(text, 0, text.length());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void arrayLengthNoEnd() {
        String text;
        text = "{1";
        PostgreArrays.parseArrayLength(text, 0, text.length());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void arrayLengthNoEnd2() {
        String text;
        text = "{\"}";
        PostgreArrays.parseArrayLength(text, 0, text.length());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void arrayLengthNoEnd3() {
        String text;
        text = "{{}";
        PostgreArrays.parseArrayLength(text, 0, text.length());
    }

    @Test
    public void parseArray() {
        String text;
        Class<?> javaType;
        Object array;

        text = "{}";
        javaType = int[].class;
        array = PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[0]);

        text = "{1}";
        array = PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[]{1});

        text = "{1 , 2}";
        array = PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[]{1, 2});

        text = "{{1 , 2}}";
        javaType = int[][].class;
        array = PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[][]{{1, 2}});

        text = "{{1 , 2} , { 3, 4}}";
        array = PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[][]{{1, 2}, {3, 4}});

        text = "[-1:0][-2:-1]={{1 , 2} , { 3, 4}}";
        array = PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[][]{{1, 2}, {3, 4}});


        text = "{}";
        javaType = String[].class;
        array = PostgreArrays.parseArrayText(javaType, text, false, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[0]);

        text = "{\"\"}";
        array = PostgreArrays.parseArrayText(javaType, text, false, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[]{""});

        text = "{\"I love \\\"army\\\"\"}";
        array = PostgreArrays.parseArrayText(javaType, text, false, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[]{"I love \\\"army\\\""});

        text = "{{\"\"}}";
        javaType = String[][].class;
        array = PostgreArrays.parseArrayText(javaType, text, false, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[][]{{""}});

        text = "{{\"\"}, {army} }";
        array = PostgreArrays.parseArrayText(javaType, text, false, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[][]{{""}, {"army"}});

        text = "{{\"I love \\\"army\\\"\"} , {\"My Name is zoro.\"} }";
        array = PostgreArrays.parseArrayText(javaType, text, false, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[][]{{"I love \\\"army\\\""}, {"My Name is zoro."}});
    }

    @Test
    public void parseArrayLengthMap() {
        String text;
        Map<Class<?>, Integer> map;

        text = "[1:1]";
        map = PostgreArrays.parseArrayLengthMap(int[].class, text, 0, text.length());
        Assert.assertEquals(map.size(), 1);
        Assert.assertEquals(map.get(int[].class), Integer.valueOf(1));

        text = " [ 1 : 1 ]";
        map = PostgreArrays.parseArrayLengthMap(int[].class, text, 0, text.length());
        Assert.assertEquals(map.size(), 1);
        Assert.assertEquals(map.get(int[].class), Integer.valueOf(1));

        text = "[1:1][-2:-1]";
        map = PostgreArrays.parseArrayLengthMap(int[][].class, text, 0, text.length());
        Assert.assertEquals(map.size(), 2);
        Assert.assertEquals(map.get(int[][].class), Integer.valueOf(1));
        Assert.assertEquals(map.get(int[].class), Integer.valueOf(2));

        text = " [ 1 : 1 ] [-2:-1 ]";
        map = PostgreArrays.parseArrayLengthMap(int[][].class, text, 0, text.length());
        Assert.assertEquals(map.size(), 2);
        Assert.assertEquals(map.get(int[][].class), Integer.valueOf(1));
        Assert.assertEquals(map.get(int[].class), Integer.valueOf(2));

    }

    @Test
    public void nonNullError() {
        String text;
        final Class<?> javaType;
        javaType = int[].class;
        try {
            text = "{null}";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

        try {
            text = "{NULL}";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

        try {
            text = "{ null  }";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

        try {
            text = "{ NULL  }";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

        //

        try {
            text = "{1,null}";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

        try {
            text = "{2 , null}";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

        try {
            text = "{ 3,null  }";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

        try {
            text = "{ 4 , null  }";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

        try {
            text = "{ 4 , null  }";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

        //
        try {
            text = "{1,NULL}";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

        try {
            text = "{2 , NULL}";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

        try {
            text = "{ 3,NULL  }";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

        try {
            text = "{ 4 , NULL  }";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

        try {
            text = "{ 4 , NULL  }";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

    }

    @Test
    public void memberArrayNullError() {
        String text;
        final Class<?> javaType;
        javaType = int[][].class;
        try {
            text = "{{1},null}";
            PostgreArrays.parseArrayText(javaType, text, true, _Constant.COMMA, this::parseInt);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }
    }


    private Object parseInt(String text, int offset, int end) {
        return Integer.parseInt(text.substring(offset, end));
    }


}
