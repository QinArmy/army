package io.army.mapping.postgre;

import io.army.dialect._Constant;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * <p>
 * This class unit test class of {@link PostgreArrayParsers}.
 * </p>
 *
 * @see PostgreArrayParsers
 */
@Test
public class PostgreArrayParsersUnitTests {


    /**
     * @see PostgreArrayParsers#parseArrayLength(String, int, int)
     */
    @Test
    public void arrayLength() {
        String text;
        int arrayLength;

        text = "{}";
        arrayLength = PostgreArrayParsers.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 0);

        text = "{1}";
        arrayLength = PostgreArrayParsers.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 1);

        text = "{ 1 ,2}";
        arrayLength = PostgreArrayParsers.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 2);

        text = "{{ 1 ,2}}";
        arrayLength = PostgreArrayParsers.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 1);

        text = "{{ 1 ,2} , {3,4}}";
        arrayLength = PostgreArrayParsers.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 2);


        text = "{\"\"}";
        arrayLength = PostgreArrayParsers.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 1);

        text = "{\"\"\"\"}";
        arrayLength = PostgreArrayParsers.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 1);

        text = "{\"\\\"\"}";
        arrayLength = PostgreArrayParsers.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 1);

        text = "{{\"meeting\", \"lunch\"}, {\"training\", \"presentation\"}}";
        arrayLength = PostgreArrayParsers.parseArrayLength(text, 0, text.length());
        Assert.assertEquals(arrayLength, 2);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void arrayLengthNoStart() {
        String text;
        text = "1}";
        PostgreArrayParsers.parseArrayLength(text, 0, text.length());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void arrayLengthNoEnd() {
        String text;
        text = "{1";
        PostgreArrayParsers.parseArrayLength(text, 0, text.length());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void arrayLengthNoEnd2() {
        String text;
        text = "{\"}";
        PostgreArrayParsers.parseArrayLength(text, 0, text.length());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void arrayLengthNoEnd3() {
        String text;
        text = "{{}";
        PostgreArrayParsers.parseArrayLength(text, 0, text.length());
    }

    @Test
    public void parseArray() {
        String text;
        Class<?> javaType;
        Object array;

        text = "{}";
        javaType = int[].class;
        array = PostgreArrayParsers.parseArrayText(javaType, text, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[0]);

        text = "{1}";
        array = PostgreArrayParsers.parseArrayText(javaType, text, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[]{1});

        text = "{1 , 2}";
        array = PostgreArrayParsers.parseArrayText(javaType, text, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[]{1, 2});

        text = "{{1 , 2}}";
        javaType = int[][].class;
        array = PostgreArrayParsers.parseArrayText(javaType, text, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[][]{{1, 2}});

        text = "{{1 , 2} , { 3, 4}}";
        array = PostgreArrayParsers.parseArrayText(javaType, text, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[][]{{1, 2}, {3, 4}});


        text = "{}";
        javaType = String[].class;
        array = PostgreArrayParsers.parseArrayText(javaType, text, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[0]);

        text = "{\"\"}";
        array = PostgreArrayParsers.parseArrayText(javaType, text, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[]{""});

        text = "{\"I love \\\"army\\\"\"}";
        array = PostgreArrayParsers.parseArrayText(javaType, text, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[]{"I love \\\"army\\\""});

        text = "{{\"\"}}";
        javaType = String[][].class;
        array = PostgreArrayParsers.parseArrayText(javaType, text, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[][]{{""}});

        text = "{{\"\"}, {army} }";
        array = PostgreArrayParsers.parseArrayText(javaType, text, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[][]{{""}, {"army"}});

        text = "{{\"I love \\\"army\\\"\"} , {\"My Name is zoro.\"} }";
        array = PostgreArrayParsers.parseArrayText(javaType, text, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[][]{{"I love \\\"army\\\""}, {"My Name is zoro."}});
    }


    private Object parseInt(String text, int offset, int end) {
        return Integer.parseInt(text.substring(offset, end));
    }


}
