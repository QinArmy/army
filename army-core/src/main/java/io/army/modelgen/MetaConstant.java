package io.army.modelgen;

import io.army.util.ArrayUtils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Set;

/**
 * Meta Constant set
 */
public interface MetaConstant {

    String TABLE_META = "T";

    String TABLE_NAME = "TABLE_NAME";

    String FIELD_COUNT = "FIELD_COUNT";

    String FIELD_TOTAL = "FIELD_TOTAL";

    String META_CLASS_NAME_SUFFIX = "_";

    Set<Class<?>> MAYBE_NO_DEFAULT_TYPES = ArrayUtils.asUnmodifiableSet(
            String.class,
            Long.class,
            Integer.class,
            BigDecimal.class,

            BigInteger.class,
            Byte.class,
            Double.class,
            Float.class,

            LocalTime.class,
            Short.class,
            LocalDateTime.class,
            LocalDate.class,

            ZonedDateTime.class
    );

    Set<Class<?>> WITHOUT_DEFAULT_TYPES = ArrayUtils.asUnmodifiableSet(
            MAYBE_NO_DEFAULT_TYPES,
            InputStream.class,
            byte[].class
    );
}
