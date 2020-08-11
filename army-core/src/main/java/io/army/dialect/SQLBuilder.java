package io.army.dialect;

public interface SQLBuilder {

    SQLBuilder append(boolean b);

    SQLBuilder append(char ch);

    SQLBuilder append(char[] charArray);

    SQLBuilder append(char[] charArray, int offset, int len);

    SQLBuilder append(CharSequence s);

    SQLBuilder append(CharSequence s, int start, int end);

    SQLBuilder append(double d);

    SQLBuilder append(float f);

    SQLBuilder append(int i);

    SQLBuilder append(long lng);

    SQLBuilder append(Object obj);

    SQLBuilder append(String str);

    SQLBuilder appendCodePoint(int codePoint);

    @Override
    String toString();
}
