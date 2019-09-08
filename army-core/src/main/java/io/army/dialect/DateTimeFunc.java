package io.army.dialect;

public interface DateTimeFunc {

    String now();

    String now(int precision);

    String currentDate();

    String currentTime();

    String currentTime(int precision);

}
