package io.army.modelgen;


import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.*;
import java.util.Locale;
import java.util.Set;

/**
 * Meta Constant set
 */
public abstract class MetaConstant {


    private MetaConstant() {
        throw new UnsupportedOperationException();
    }


    public static final String ID = "id";
    public static final String CREATE_TIME = "createTime";
    public static final String VISIBLE = "visible";
    public static final String UPDATE_TIME = "updateTime";

    public static final String VERSION = "version";

    public static final Set<String> DOMAIN_PROPS = CollectionUtils.asUnmodifiableSet(ID, CREATE_TIME);
    public static final Set<String> UPDATE_PROPS = CollectionUtils.asUnmodifiableSet(ID, CREATE_TIME, UPDATE_TIME);
    public static final Set<String> RESERVED_PROPS = CollectionUtils.asUnmodifiableSet(
            ID, CREATE_TIME, VISIBLE, UPDATE_TIME, VERSION);


    public static final String TABLE_META = "T";

    public static final String TABLE_NAME = "TABLE_NAME";

    public static final String FIELD_COUNT = "FIELD_COUNT";

    public static final String FIELD_TOTAL = "FIELD_TOTAL";

    public static final String META_CLASS_NAME_SUFFIX = "_";

    public static final Set<Class<?>> SIMPLE_JAVA_TYPE_SET = CollectionUtils.asUnmodifiableSet(
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

            ZonedDateTime.class,
            OffsetDateTime.class,
            OffsetTime.class,
            Year.class,

            YearMonth.class,
            MonthDay.class
    );

    public static final Set<Class<?>> MAYBE_NO_DEFAULT_TYPES = SIMPLE_JAVA_TYPE_SET;

    public static final Set<Class<?>> WITHOUT_DEFAULT_TYPES = CollectionUtils.asUnmodifiableSet(
            MAYBE_NO_DEFAULT_TYPES,
            InputStream.class,
            Reader.class,
            Path.class,
            byte[].class
    );


    public static String camelToUpperCase(String camel) {
        return camelToUnderline(camel).toUpperCase(Locale.ROOT);
    }

    public static String camelToLowerCase(String camel) {
        return camelToUnderline(camel).toLowerCase(Locale.ROOT);
    }

    private static String camelToUnderline(final String camel) {
        final int len = camel.length();
        final StringBuilder builder = new StringBuilder(camel.length() + 5);
        char ch;
        int preIndex = 0;
        for (int i = 0; i < len; i++) {
            ch = camel.charAt(i);
            if (Character.isUpperCase(ch)) {
                builder.append(camel, preIndex, i);
                builder.append('_');
                preIndex = i;
            }
        }
        builder.append(camel, preIndex, len);
        return builder.toString();
    }


}
