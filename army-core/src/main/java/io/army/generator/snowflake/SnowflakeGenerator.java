package io.army.generator.snowflake;

import io.army.annotation.Param;
import io.army.bean.ReadWrapper;
import io.army.generator.FieldGenerator;
import io.army.generator.FieldGeneratorUtils;
import io.army.generator.GeneratorException;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.meta.MetaException;
import io.army.util.ClassUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.time.temporal.ChronoField.*;


/**
 * @see SnowflakeClient
 */
public final class SnowflakeGenerator implements FieldGenerator {

    public static SnowflakeGenerator create(final FieldMeta<?> field, final SnowflakeClient client) {
        final GeneratorMeta meta;
        meta = field.generator();
        if (meta == null) {
            throw FieldGeneratorUtils.noGeneratorMeta(field);
        }
        final Map<String, String> paramMap;
        paramMap = meta.params();
        final long startTime;
        try {
            startTime = Long.parseLong(paramMap.get(START_TIME));
            if (startTime < 0L) {
                String m = String.format("%s parameter %s must non-negative.", field, START_TIME);
                throw new MetaException(m);
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            String m = String.format("%s parameter %s config error.", field, START_TIME);
            throw new MetaException(m, e);

        }
        final Class<?> javaType = field.javaType();
        if (javaType != Long.class && javaType != BigInteger.class && javaType != String.class) {
            throw FieldGeneratorUtils.dontSupportJavaType(SnowflakeGenerator.class, field);
        }
        final FieldMeta<?> dependField;
        dependField = field.dependField();
        if (dependField != null) {
            if (dependField.javaType() != Long.class) {
                String m = String.format("%s couldn't depend java type %s", field, Long.class.getName());
                throw new MetaException(m);
            }

            if (javaType == Long.class) {
                String m = String.format("%s java type is %s,couldn't depend any depend field."
                        , field, Long.class.getName());
                throw new MetaException(m);
            }
        }
        return INSTANCE_MAP.computeIfAbsent(startTime, time -> {
            final Worker worker;
            worker = client.currentWorker();
            final Snowflake snowflake;
            snowflake = Snowflake.create(time, worker.dataCenterId, worker.workerId);
            final SnowflakeGenerator generator = new SnowflakeGenerator(snowflake);
            client.registerGenerator(generator, generator::updateWorker);
            return generator;
        });

    }


    private static final ConcurrentMap<Long, SnowflakeGenerator> INSTANCE_MAP = new ConcurrentHashMap<>();


    /**
     * @see Param
     */
    public static final String START_TIME = "startTime";

    public static final String DATE = "date";

    public static final String SUFFIX_LENGTH = "suffixLength";


    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.NEVER)
            .appendValue(MONTH_OF_YEAR, 2)
            .appendValue(DAY_OF_MONTH, 2)
            .toFormatter(Locale.ENGLISH);

    public final long startTime;


    private Snowflake snowflake;

    private SnowflakeGenerator(Snowflake snowflake) {
        this.snowflake = snowflake;
        this.startTime = snowflake.startTime;
    }

    @Override
    public Object next(final FieldMeta<?> field, final ReadWrapper domain) throws GeneratorException {
        final Class<?> javaType = field.javaType();
        final Object nextSequence;
        if (javaType == Long.class) {
            nextSequence = this.snowflake.next();
        } else if (javaType == BigInteger.class) {
            nextSequence = new BigInteger(this.nextAsString(field, domain));
        } else if (javaType == String.class) {
            nextSequence = this.nextAsString(field, domain);
        } else {
            throw FieldGeneratorUtils.dontSupportJavaType(SnowflakeGenerator.class, field);
        }
        return nextSequence;
    }


    private String nextAsString(final FieldMeta<?> field, final ReadWrapper domain)
            throws GeneratorException {

        GeneratorMeta meta;
        meta = field.generator();
        assert meta != null;
        final Map<String, String> paramMap = meta.params();
        final FieldMeta<?> dependField;
        dependField = field.dependField();

        String suffix = null;
        if (dependField != null) {
            suffix = getSuffix(field, dependField, paramMap, domain);
        }

        final String snowSequence;
        snowSequence = Long.toString(this.snowflake.next());
        final boolean hasDate;
        hasDate = "true".equals(meta.params().get(DATE));
        final String sequence;
        if (suffix == null && !hasDate) {
            sequence = snowSequence;
        } else {
            final StringBuilder builder;
            if (hasDate && suffix != null) {
                builder = new StringBuilder(suffix.length() + 8 + snowSequence.length());
            } else if (hasDate) {
                builder = new StringBuilder(8 + snowSequence.length());
            } else {
                builder = new StringBuilder(suffix.length() + snowSequence.length());
            }
            if (hasDate) {
                builder.append(LocalDateTime.now().format(FORMATTER));
            }
            builder.append(snowSequence);
            if (suffix != null) {
                builder.append(suffix);
            }
            sequence = builder.toString();
        }
        return sequence;
    }

    private void updateWorker(final Worker worker) {
        synchronized (this) {
            final Snowflake snowflake = this.snowflake;
            if (worker.dataCenterId == snowflake.dataCenterId && worker.workerId == snowflake.workerId) {
                return;
            }
            this.snowflake = Snowflake.create(this.startTime, worker.dataCenterId, worker.workerId);
        }
    }


    private static String suffixWithZero(final int expectedLength, final String dependValue) {
        final int length = dependValue.length();
        final String suffix;
        if (length == expectedLength) {
            suffix = dependValue;
        } else if (length > expectedLength) {
            suffix = dependValue.substring(length - expectedLength);
        } else {
            final char[] charArray = new char[expectedLength];
            final int boundary = expectedLength - length;
            for (int i = 0; i < boundary; i++) {
                charArray[i] = '0';
            }
            final char[] valueChars = dependValue.toCharArray();
            System.arraycopy(valueChars, 0, charArray, boundary, valueChars.length);
            suffix = new String(charArray);

        }
        return suffix;
    }


    private static String getSuffix(FieldMeta<?> field, FieldMeta<?> dependField, Map<String, String> paramMap
            , ReadWrapper domain) {
        final int length;
        length = getSuffixLength(field, paramMap);
        final Object dependValue;
        dependValue = domain.get(dependField.fieldName());
        final String suffix;
        if (dependValue instanceof Long) {
            suffix = suffixWithZero(length, Long.toString((Long) dependValue));
        } else if (dependValue instanceof String) {
            suffix = suffixWithZero(length, (String) dependValue);
        } else if (dependValue instanceof Integer) {
            suffix = suffixWithZero(length, Integer.toString((Integer) dependValue));
        } else if (dependValue instanceof BigInteger) {
            suffix = suffixWithZero(length, dependValue.toString());
        } else {
            String m = String.format("%s depend field %s type %s not in [%s,%s,%s,%s]."
                    , field, dependField, ClassUtils.safeClassName(dependValue)
                    , Long.class.getName(), String.class.getName()
                    , Integer.class.getName(), BigInteger.class.getName());
            throw new GeneratorException(m);
        }
        return suffix;
    }

    private static int getSuffixLength(FieldMeta<?> field, Map<String, String> paramMap) {
        final String suffixLengthText;
        suffixLengthText = paramMap.get(SUFFIX_LENGTH);
        final int length;
        if (suffixLengthText == null) {
            length = 5;
        } else {
            try {
                length = Integer.parseInt(suffixLengthText);
            } catch (NumberFormatException e) {
                String m = String.format("%s %s[%s] error.", field, SUFFIX_LENGTH, suffixLengthText);
                throw new GeneratorException(m, e);
            }
            if (length < 1 || length > 19) {
                String m = String.format("%s %s[%s] error.", field, SUFFIX_LENGTH, suffixLengthText);
                throw new GeneratorException(m);
            }
        }
        return length;
    }


}
