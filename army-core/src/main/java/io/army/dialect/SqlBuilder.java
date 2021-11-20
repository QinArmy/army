package io.army.dialect;

public interface SqlBuilder {

    SqlBuilder append(boolean b);

    SqlBuilder append(char ch);

    SqlBuilder append(char[] charArray);

    SqlBuilder append(char[] charArray, int offset, int len);

    SqlBuilder append(CharSequence s);

    SqlBuilder append(CharSequence s, int start, int end);

    SqlBuilder append(double d);

    SqlBuilder append(float f);

    SqlBuilder append(int i);

    SqlBuilder append(long lng);

    SqlBuilder append(Object obj);

    SqlBuilder append(String str);

    SqlBuilder appendCodePoint(int codePoint);

    @Override
    String toString();
}
