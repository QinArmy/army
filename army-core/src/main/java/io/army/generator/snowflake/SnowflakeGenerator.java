package io.army.generator.snowflake;

import io.army.annotation.Params;
import io.army.beans.ReadonlyWrapper;
import io.army.env.Environment;
import io.army.generator.PreMultiGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.util.Assert;
import io.army.util.ReflectionUtils;
import io.army.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;


/**
 * <p>
 * blow options :
 *     <ul>
 *         <li>{@link Snowflake } implementation class name</li>
 *         <li>{@link SnowflakeClient } instance</li>
 *         <li> snowflake default start time</li>
 *     </ul>
 * </p>
 * <p>
 * <p>
 *
 * @see Snowflake
 * @see SnowflakeClient
 */
public final class SnowflakeGenerator implements PreMultiGenerator {

    /*################################## blow static properties ##################################*/

    private static final Logger LOG = LoggerFactory.getLogger(SnowflakeGenerator.class);

    /**
     * (optional) Specifies the implementation class name of {@link Snowflake}
     * , if absent the default is {@link FiveBitWorkerSnowflake}.
     *
     * @see Environment#getProperty(String, Class)
     */
    public static final String SNOWFLAKE_CLASS_NAME_KEY = SnowflakeGenerator.class.getName().concat("snowflakeClassName");

    /**
     * (required) Specifies the default start time of {@link Snowflake}
     *
     * @see Environment#getProperty(String, Class)
     */
    public static final String DEFAULT_START_TIME_KEY = SnowflakeGenerator.class.getName().concat("defaultStartTime");

    /**
     * @see Params
     */
    public static final String START_TIME = "startTime";

    /**
     * (optional) Specifies the {@link SnowflakeClient} for SnowflakeGenerator
     * , if absent {@link Worker} always is {@link Worker#ZERO}
     *
     * @see Environment#getBean(String, Class)
     */
    public static final String SNOWFLAKE_CLIENT_NAME = SnowflakeGenerator.class.getName().concat("snowflakeClient");

    /**
     * {@link #defaultSnowflake}'s default start time
     */
    public static final long DEFAULT_START_TIME_OF_DEFAULT = 1580302248761L;


    /**
     * @see #defaultSnowflake
     */
    private static final AtomicReference<Snowflake> DEFAULT_SNOWFLAKE_HOLDER = new AtomicReference<>(null);

    /**
     *
     */
    private static final AtomicReference<Method> SNOWFLAKE_BUILDER = new AtomicReference<>(null);

    /**
     * @see #getSnowflakeClient(Environment)
     */
    private static final AtomicReference<SnowflakeClient> SNOWFLAKE_CLIENT = new AtomicReference<>(null);

    /**
     * @see #doGetStartTime(Environment, FieldMeta)
     * @see #getDefaultStartTime()
     */
    private static final AtomicLong DEFAULT_START_TIME = new AtomicLong(-1);

    /**
     * @see #getInstance(FieldMeta, Environment)
     */
    private static final ConcurrentMap<Long, SnowflakeGenerator> INSTANCE_HOLDER = new ConcurrentHashMap<>();

    /**
     * @see #getDefaultSnowflake()
     * @see #DEFAULT_SNOWFLAKE_HOLDER
     */
    private static Snowflake defaultSnowflake;


    /*################################## blow static method ##################################*/

    /**
     * @see io.army.generator.MultiGenerator
     */
    public static SnowflakeGenerator getInstance(FieldMeta<?, ?> fieldMeta, Environment env) {

        SnowflakeGenerator generator = INSTANCE_HOLDER.computeIfAbsent(
                doGetStartTime(env, fieldMeta)
                , key -> new SnowflakeGenerator(createSnowflake(env, fieldMeta)));
        generator.registerGenerator(env);
        return generator;
    }

    /**
     * @see io.army.generator.MultiGenerator
     */
    public static boolean isSupported(Class<?> dependType) {
        return dependType == Long.class
                || dependType == Long.TYPE
                || dependType == String.class
                || dependType == BigInteger.class
                ;
    }

    public static Snowflake getDefaultSnowflake() {
        if (defaultSnowflake == null) {
            doUpdateDefaultSnowflake(Worker.ZERO, false);
        }
        return defaultSnowflake;
    }


    public static long getDefaultStartTime() {
        long statTime = DEFAULT_START_TIME.get();
        Assert.state(statTime >= 0, "start time not init");
        return statTime;
    }


    /**
     * @see Snowflake#next()
     */
    public static long next() {
        return getDefaultSnowflake().next();
    }

    /**
     * @see Snowflake#nextAsString()
     */
    public static String nextAsString() {
        return getDefaultSnowflake().nextAsString();
    }

    /**
     * @see Snowflake#next(long)
     */
    public static BigInteger next(long suffixNumber) {
        return getDefaultSnowflake().next(suffixNumber);
    }

    /**
     * @see Snowflake#nextAsString(long)
     */
    public static String nextAsString(long suffixNumber) {
        return getDefaultSnowflake().nextAsString(suffixNumber);
    }


    /*################################## blow package static method ##################################*/

    /**
     * package instance method for {@link AbstractSnowflakeClient}
     */
    static void updateDefaultSnowflake() {
        SnowflakeClient client = SNOWFLAKE_CLIENT.get();
        Assert.state(client != null, "SnowflakeClient not init.");

        doUpdateDefaultSnowflake(client.currentWorker(), true);
    }


    static boolean isMatchWorker(Worker worker, @Nullable Snowflake snowflake) {
        return snowflake != null
                && worker.getDataCenterId() == snowflake.getDataCenterId()
                && worker.getWorkerId() == snowflake.getWorkerId();
    }

    /*################################## blow private static method ##################################*/

    private static Snowflake createSnowflake(Environment env, FieldMeta<?, ?> fieldMeta) {

        SnowflakeClient client = getSnowflakeClient(env);
        final long startTime = doGetStartTime(env, fieldMeta);

        final Method method = getSnowflakeBuilder(env);
        Snowflake snowflake = (Snowflake) ReflectionUtils.invokeMethod(method, null, startTime, client.currentWorker());
        if (snowflake == null) {
            throw new IllegalStateException(String.format("method[%s] return null", method));
        }
        return snowflake;
    }

    private static long doGetStartTime(Environment env, FieldMeta<?, ?> fieldMeta) {
        GeneratorMeta generatorMeta = fieldMeta.generator();

        if (generatorMeta == null) {
            return doGetDefaultSnowflake(env);
        }

        long startTime = -1;
        String startTimeText = generatorMeta.params().get(START_TIME);
        if (!StringUtils.isEmpty(startTimeText)) {
            startTime = Long.parseLong(startTimeText);
            if (startTime < 0 || startTime > SystemClock.now()) {
                throw new IllegalStateException(String.format
                        ("mapping field[%s] generator start time error[%s]", fieldMeta, startTime));
            }
        }
        if (startTime < 0) {
            startTime = doGetDefaultSnowflake(env);
        }
        return startTime;
    }

    private static long doGetDefaultSnowflake(Environment env) {
        long startTime = DEFAULT_START_TIME.get();
        if (startTime >= 0) {
            return startTime;
        }
        startTime = env.getRequiredProperty(DEFAULT_START_TIME_KEY, Long.class);
        if (startTime < 0 || startTime > SystemClock.now()) {
            throw new IllegalStateException(String.format("default snowflake start time[%s] config error", startTime));
        }

        if (DEFAULT_START_TIME.compareAndSet(-1, startTime)) {
            LOG.info("default snowflake start time is {}", DEFAULT_START_TIME.get());
        }
        return DEFAULT_START_TIME.get();
    }

    private static SnowflakeClient getSnowflakeClient(Environment env) {
        SnowflakeClient client = SNOWFLAKE_CLIENT.get();
        if (client == null) {
            SNOWFLAKE_CLIENT.compareAndSet(null, env.getRequiredBean(SNOWFLAKE_CLIENT_NAME, SnowflakeClient.class));
            client = SNOWFLAKE_CLIENT.get();
        }
        return client;
    }


    private static Method getSnowflakeBuilder(Environment env) {
        Method method = SNOWFLAKE_BUILDER.get();
        if (method != null) {
            return method;
        }
        Class<?> snowflakeClass = env.getProperty(SNOWFLAKE_CLASS_NAME_KEY, Class.class, FiveBitWorkerSnowflake.class);

        method = ReflectionUtils.findMethod(snowflakeClass, "getInstance", long.class, Worker.class);
        if (method == null
                || !Modifier.isPublic(method.getModifiers())
                || !Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException(String.format("snowflake implementation[%s] class no getInstance method"
                    , snowflakeClass.getName()));
        }
        if (SNOWFLAKE_BUILDER.compareAndSet(null, method)) {
            LOG.info("snowflake implementation class is {}", snowflakeClass.getName());
        }
        return SNOWFLAKE_BUILDER.get();
    }

    private static void doUpdateDefaultSnowflake(Worker worker, boolean clientInvoker) {
        final Snowflake oldSnowflake = DEFAULT_SNOWFLAKE_HOLDER.get();

        long startTime = DEFAULT_START_TIME.get();
        if (startTime < 0 && clientInvoker) {
            throw new IllegalStateException("default stat time not init.");
        }

        if (isMatchWorker(worker, oldSnowflake)
                && startTime == oldSnowflake.getStartTime()) {
            return;
        }
        if (startTime < 0) {
            startTime = DEFAULT_START_TIME_OF_DEFAULT;
        }

        final Method method = SNOWFLAKE_BUILDER.get();
        Assert.state(method != null, "SnowflakeGenerator not init.");
        final Snowflake newSnowflake = (Snowflake) ReflectionUtils.invokeMethod(
                method, null, startTime, worker);
        Assert.state(newSnowflake != null, () -> String.format("method[%s] return null", method));

        if (DEFAULT_SNOWFLAKE_HOLDER.compareAndSet(oldSnowflake, newSnowflake)) {
            LOG.info("default snowflake update,worker[{}],Snowflake[{}]", worker, newSnowflake.getClass().getName());
        }
        // other thread maybe delay read new value
        defaultSnowflake = DEFAULT_SNOWFLAKE_HOLDER.get();
    }

    /*################################## blow instance properties ##################################*/

    private final AtomicReference<Snowflake> snowflakeHolder = new AtomicReference<>(null);

    private Snowflake snowflake;


    private SnowflakeGenerator(Snowflake snowflake) {
        this.snowflake = snowflake;
        snowflakeHolder.set(this.snowflake);
    }

    /*################################## blow interface method ##################################*/

    @Override
    public Object next(FieldMeta<?, ?> fieldMeta, ReadonlyWrapper entityWrapper) {
        Object identifier;

        if (fieldMeta.javaType() == Long.class) {
            identifier = snowflake.next();
        } else if (fieldMeta.javaType() == String.class) {
            if (fieldMeta.precision() >=0 && fieldMeta.precision() <= 19) {
                identifier = snowflake.nextAsString();
            } else {
                identifier = nextAsStringWithDepend(fieldMeta, entityWrapper);
            }
        } else if (fieldMeta.javaType() == BigInteger.class) {
            identifier = new BigInteger(nextAsStringWithDepend(fieldMeta, entityWrapper));
        } else {
            throw new IllegalArgumentException(String.format("SnowflakeGenerator unsupported java type[%s]"
                    , fieldMeta.javaType().getName()));
        }
        return identifier;
    }

    public final Snowflake getSnowflake() {
        return this.snowflake;
    }

    private String nextAsStringWithDepend(FieldMeta<?, ?> fieldMeta, ReadonlyWrapper entityWrapper) {
        GeneratorMeta generatorMeta = fieldMeta.generator();
        Assert.notNull(generatorMeta, "fieldMeta must have generator");

        String dependOnProp = generatorMeta.dependPropName();
        if (!StringUtils.hasText(dependOnProp)) {
            return snowflake.nextAsString();
        }
        Assert.isTrue(entityWrapper.isReadableProperty(dependOnProp)
                , () -> String.format("fieldMeta[%s.%s] depend not readable"
                        , fieldMeta.javaType().getName(), fieldMeta.propertyName()));

        Object dependValue = entityWrapper.getPropertyValue(dependOnProp);

        long longValue;
        if (dependValue instanceof Number) {
            longValue = ((Number) dependValue).longValue();
        } else if (dependValue instanceof String) {
            longValue = tryConvertToLong((String) dependValue);
        } else {
            throw createNotSupportDependException(dependValue);
        }
        return snowflake.nextAsString(longValue);
    }

    /*################################## blow private method ##################################*/

    private long tryConvertToLong(String dependValue) {
        try {
            String text = dependValue;
            if (text.length() > 5) {
                text = text.substring(text.length() - 5);
            }
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format(
                    "depend value[%s] cannot convert to long.", dependValue));
        }
    }

    private IllegalArgumentException createNotSupportDependException(@Nullable Object dependValue) {
        String type;
        if (dependValue == null) {
            type = null;
        } else {
            type = dependValue.getClass().getName();
        }
        return new IllegalArgumentException(String.format("%s cannot support depend type[%s]"
                , SnowflakeGenerator.class.getName(), type));
    }

    /**
     * @see SnowflakeClient#registerGenerator(SnowflakeGenerator)
     */
    private void registerGenerator(Environment env) {
        SnowflakeClient client = getSnowflakeClient(env);
        for (int i = 0; !client.registerGenerator(this); i++) {
            updateSnowflake();
            Assert.state(i <= 10, "SnowflakeGenerator register count exception");
        }
    }

    /**
     * package instance method for {@link AbstractSnowflakeClient}
     */
    void updateSnowflake() {
        SnowflakeClient client = SNOWFLAKE_CLIENT.get();
        Assert.state(client != null, "SnowflakeClient not init");

        final Snowflake oldSnowflake = snowflakeHolder.get();
        final Worker worker = client.currentWorker();

        if (isMatchWorker(worker, oldSnowflake)) {
            return;
        }

        final Method method = SNOWFLAKE_BUILDER.get();

        Assert.state(method != null, "SnowflakeGenerator not init");
        Snowflake newSnowflake = (Snowflake) ReflectionUtils.invokeMethod(method, null
                , oldSnowflake.getStartTime(), worker);

        Assert.state(newSnowflake != null, () -> String.format("method[%s] return null", method));
        snowflakeHolder.compareAndSet(oldSnowflake, newSnowflake);
        this.snowflake = snowflakeHolder.get();
    }


}
