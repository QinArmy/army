/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.example.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Locale;
import java.util.Random;

import static java.time.temporal.ChronoField.*;

public abstract class CommonUtils {


    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.NORMAL)
            .appendValue(MONTH_OF_YEAR, 2)
            .appendValue(DAY_OF_MONTH, 2)
            .toFormatter(Locale.ENGLISH);


    protected CommonUtils() {
        throw new UnsupportedOperationException();
    }


    public static LocalDate birthdayFrom(String certificateNo) {
        return LocalDate.parse(certificateNo.substring(6, 14), DATE_FORMATTER);
    }

    public static String randomCaptcha() {
        final Random random = new Random();
        return Integer.toString(random.nextInt(99999));
    }

}
