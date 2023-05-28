package io.army.mapping.postgre;

import io.army.dialect._Constant;
import io.army.mapping.optional.PostgreArrays;
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
public class PostgreArrayParsersUnitTests {


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
        array = PostgreArrays.parseArrayText(javaType, text, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[0]);

        text = "{1}";
        array = PostgreArrays.parseArrayText(javaType, text, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[]{1});

        text = "{1 , 2}";
        array = PostgreArrays.parseArrayText(javaType, text, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[]{1, 2});

        text = "{{1 , 2}}";
        javaType = int[][].class;
        array = PostgreArrays.parseArrayText(javaType, text, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[][]{{1, 2}});

        text = "{{1 , 2} , { 3, 4}}";
        array = PostgreArrays.parseArrayText(javaType, text, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[][]{{1, 2}, {3, 4}});

        text = "[-1:0][-2:-1]={{1 , 2} , { 3, 4}}";
        array = PostgreArrays.parseArrayText(javaType, text, _Constant.COMMA, this::parseInt);
        Assert.assertEquals(array, new int[][]{{1, 2}, {3, 4}});


        text = "{}";
        javaType = String[].class;
        array = PostgreArrays.parseArrayText(javaType, text, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[0]);

        text = "{\"\"}";
        array = PostgreArrays.parseArrayText(javaType, text, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[]{""});

        text = "{\"I love \\\"army\\\"\"}";
        array = PostgreArrays.parseArrayText(javaType, text, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[]{"I love \\\"army\\\""});

        text = "{{\"\"}}";
        javaType = String[][].class;
        array = PostgreArrays.parseArrayText(javaType, text, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[][]{{""}});

        text = "{{\"\"}, {army} }";
        array = PostgreArrays.parseArrayText(javaType, text, _Constant.COMMA, String::substring);
        Assert.assertEquals(array, new String[][]{{""}, {"army"}});

        text = "{{\"I love \\\"army\\\"\"} , {\"My Name is zoro.\"} }";
        array = PostgreArrays.parseArrayText(javaType, text, _Constant.COMMA, String::substring);
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


    private Object parseInt(String text, int offset, int end) {
        return Integer.parseInt(text.substring(offset, end));
    }


}
