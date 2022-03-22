package io.army.generator.snowflake;

import io.army.annotation.Param;
import io.army.bean.ReadWrapper;
import io.army.generator.FieldGenerator;
import io.army.generator.FieldGeneratorUtils;
import io.army.generator.GeneratorException;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.meta.MetaException;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Arrays;
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
            final FieldMeta<?> dependField;
            dependField = field.dependField();
            if (dependField == null) {
                nextSequence = BigInteger.valueOf(this.snowflake.next());
            } else {
                nextSequence = new BigInteger(this.nextAsString(field, dependField, domain));
            }
        } else if (javaType == String.class) {
            final FieldMeta<?> dependField;
            dependField = field.dependField();
            if (dependField == null) {
                nextSequence = Long.toString(this.snowflake.next());
            } else {
                nextSequence = this.nextAsString(field, dependField, domain);
            }
        } else {
            throw FieldGeneratorUtils.dontSupportJavaType(SnowflakeGenerator.class, field);
        }
        return nextSequence;
    }


    private String nextAsString(final FieldMeta<?> field, FieldMeta<?> dependField, final ReadWrapper domain)
            throws GeneratorException {
        final Object dependValue;
        dependValue = domain.get(dependField.fieldName());
        if (!(dependValue instanceof Long)) {
            String m = String.format("%s depend field %s value isn't %s type."
                    , field, dependField, Long.class.getName());
            throw new GeneratorException(m);
        }
        final String suffix;
        suffix = suffixWithZero((Long) dependValue);
        final StringBuilder builder;

        GeneratorMeta meta;
        meta = field.generator();
        assert meta != null;
        if ("true".equals(meta.params().get(DATE))) {
            builder = new StringBuilder(27 + suffix.length());
            builder.append(LocalDateTime.now().format(FORMATTER));
        } else {
            builder = new StringBuilder(19 + suffix.length());
        }
        final long nextSequence;
        nextSequence = this.snowflake.next();
        return builder
                .append(nextSequence)
                .append(suffix)
                .toString();
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


    private static String suffixWithZero(long number) {
        String str = Long.toString(number % 10_0000);
        if (str.length() < 5) {
            char[] chars = new char[5 - str.length()];
            Arrays.fill(chars, '0');
            str = new String(chars) + str;
        }
        return str;
    }


}
