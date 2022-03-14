package io.army.dialect;

public abstract class DDLUtils {

    protected DDLUtils() {
        throw new UnsupportedOperationException();
    }


    public static String escapeQuote(String text) {
        return text.replaceAll("'", "\\\\'");
    }


}
