package io.army.generator.snowflake;

import io.army.ArmyKey;
import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.annotation.Param;
import io.army.beans.ArmyBean;
import io.army.beans.ReadWrapper;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.generator.PreFieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.meta.MetaException;
import io.army.session.DialectSessionFactory;
import io.army.session.FactoryMode;
import io.army.session.GenericSessionFactory;
import io.army.session.GenericTmSessionFactory;
import io.army.util.ReflectionUtils;
import io.army.util.StringUtils;
import io.army.util._Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
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
public final class SnowflakeGenerator implements PreFieldGenerator, ArmyBean {

    /*################################## blow static properties ##################################*/

    private static final Logger LOG = LoggerFactory.getLogger(SnowflakeGenerator.class);

    /**
     * @see Param
     */
    public static final String START_TIME = "startTime";

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
     *
     */
    private static final AtomicReference<SnowflakeClient> SNOWFLAKE_CLIENT = new AtomicReference<>(null);

    /**
     * @see #doGetStartTime(ArmyEnvironment, FieldMeta)
     * @see #getDefaultStartTime()
     */
    private static final AtomicLong DEFAULT_START_TIME = new AtomicLong(-1);

    /**
     * @see #build(FieldMeta, GenericSessionFactory)
     */
    private static final ConcurrentMap<Long, SnowflakeGenerator> INSTANCE_HOLDER = new ConcurrentHashMap<>();

    /**
     * @see #getDefaultSnowflake()
     * @see #DEFAULT_SNOWFLAKE_HOLDER
     */
    private static Snowflake defaultSnowflake;


    /*################################## blow static method ##################################*/

    /**
     * @see FieldGenerator
     */
    public static SnowflakeGenerator build(FieldMeta<?, ?> fieldMeta, GenericSessionFactory sessionFactory) {
        SnowflakeGenerator generator = INSTANCE_HOLDER.computeIfAbsent(
                doGetStartTime(sessionFactory.environment(), fieldMeta)
                , key -> new SnowflakeGenerator(createSnowflake(sessionFactory, fieldMeta)));
        generator.registerGenerator(sessionFactory);
        return generator;
    }

    /**
     * @see FieldGenerator
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
        _Assert.state(statTime >= 0, "start time not init");
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
        _Assert.state(client != null, "SnowflakeClient not init.");

        doUpdateDefaultSnowflake(client.currentWorker(), true);
    }


    static boolean isMatchWorker(Worker worker, @Nullable Snowflake snowflake) {
        return snowflake != null
                && worker.getDataCenterId() == snowflake.getDataCenterId()
                && worker.getWorkerId() == snowflake.getWorkerId();
    }

    /*################################## blow private static method ##################################*/

    private static Snowflake createSnowflake(GenericSessionFactory sessionFactory, FieldMeta<?, ?> fieldMeta) {

        SnowflakeClient client = getSnowflakeClient(sessionFactory);
        final long startTime = doGetStartTime(sessionFactory.environment(), fieldMeta);

        final Method method = getSnowflakeBuilder(sessionFactory.environment());
        Snowflake snowflake = (Snowflake) ReflectionUtils.invokeMethod(method, null, startTime, client.currentWorker());
        if (snowflake == null) {
            throw new IllegalStateException(String.format("method[%s] return null", method));
        }
        return snowflake;
    }

    private static long doGetStartTime(ArmyEnvironment env, FieldMeta<?, ?> fieldMeta) {
        GeneratorMeta generatorMeta = fieldMeta.generator();

        if (generatorMeta == null) {
            throw new MetaException(ErrorCode.META_ERROR, "FieldMeta[%s] GeneratorMeta is null,meta error.");
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
            startTime = doGetDefaultSnowflakeStartTime(env);
        }
        return startTime;
    }

    private static long doGetDefaultSnowflakeStartTime(ArmyEnvironment env) {
        long startTime = DEFAULT_START_TIME.get();
        if (startTime >= 0) {
            return startTime;
        }
        startTime = env.get(ArmyKey.SNOWFLAKE_DEFAULT_TIME, Long.class
                , DEFAULT_START_TIME_OF_DEFAULT);
        if (startTime < 0 || startTime > SystemClock.now()) {
            throw new IllegalStateException(String.format("default snowflake start time[%s] config error", startTime));
        }

        if (DEFAULT_START_TIME.compareAndSet(-1, startTime)) {
            LOG.info("default snowflake start time is {}", DEFAULT_START_TIME.get());
        }
        return DEFAULT_START_TIME.get();
    }

    private static SnowflakeClient getSnowflakeClient(GenericSessionFactory sessionFactory) {
        SnowflakeClient client = SNOWFLAKE_CLIENT.get();
        if (client != null) {
            return client;
        }
        ArmyEnvironment env = sessionFactory.environment();
        String beanName = env.getRequiredProperty(ArmyKey.SNOWFLAKE_CLIENT_NAME);
        client = env.getBean(beanName, SnowflakeClient.class);
        final boolean noSharding;
        if (sessionFactory instanceof GenericTmSessionFactory) {
            noSharding = false;
        } else {
            noSharding = ((DialectSessionFactory) sessionFactory).factoryMode() == FactoryMode.NO_SHARDING;
        }
        if (client == null && noSharding) {
            boolean singleApplication = env.get(
                    String.format(ArmyKey.SINGLE_APPLICATION, sessionFactory.name())
                    , Boolean.class, Boolean.TRUE);
            if (singleApplication) {
                client = SingleApplicationSnowflakeClient.build(sessionFactory);
                client.askAssignWorker();
            }
        }
        if (client == null) {
            throw new SnowflakeWorkerException("not found %s .", SnowflakeClient.class.getName());
        }

        SNOWFLAKE_CLIENT.compareAndSet(null, client);
        client = SNOWFLAKE_CLIENT.get();
        return client;
    }


    private static Method getSnowflakeBuilder(ArmyEnvironment env) {
        Method method = SNOWFLAKE_BUILDER.get();
        if (method != null) {
            return method;
        }
        final String className = env.get(ArmyKey.SNOWFLAKE_CLASS
                , FiveBitWorkerSnowflake.class.getName());
        try {
            Class<?> snowflakeClass = Class.forName(className);
            if (!Snowflake.class.isAssignableFrom(snowflakeClass)) {
                throw new ArmyRuntimeException(ErrorCode.META_ERROR, "snowflakeClass[%s] isn't %s type.", className);
            }
            method = ReflectionUtils.findMethod(snowflakeClass, "build", long.class, Worker.class);
            if (method == null
                    || !Modifier.isPublic(method.getModifiers())
                    || !Modifier.isStatic(method.getModifiers())
                    || !snowflakeClass.isAssignableFrom(method.getReturnType())) {
                throw new IllegalStateException(String.format("snowflake implementation[%s] class no build method"
                        , snowflakeClass.getName()));
            }
            if (SNOWFLAKE_BUILDER.compareAndSet(null, method)) {
                LOG.info("snowflake implementation class is {}", className);
            }
            return SNOWFLAKE_BUILDER.get();
        } catch (ClassNotFoundException e) {
            throw new ArmyRuntimeException(ErrorCode.META_ERROR, "not found snowflakeClass[%s]", className);
        }
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

        Snowflake newSnowflake;
        final Method method = SNOWFLAKE_BUILDER.get();
        if (method == null) {
            newSnowflake = FiveBitWorkerSnowflake.build(startTime, worker);
        } else {
            newSnowflake = (Snowflake) ReflectionUtils.invokeMethod(
                    method, null, startTime, worker);
            _Assert.state(newSnowflake != null, () -> String.format("method[%s] return null", method));
        }

        if (DEFAULT_SNOWFLAKE_HOLDER.compareAndSet(oldSnowflake, newSnowflake)) {
            LOG.info("default snowflake singleUpdate,worker[{}],Snowflake[{}]", worker, newSnowflake.getClass().getName());
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
    public Object next(FieldMeta<?, ?> fieldMeta, ReadWrapper domain) {
        Object identifier;

        if (fieldMeta.javaType() == Long.class) {
            identifier = snowflake.next();
        } else if (fieldMeta.javaType() == String.class) {
            if (fieldMeta.precision() >= 0 && fieldMeta.precision() <= 19) {
                identifier = snowflake.nextAsString();
            } else {
                identifier = nextAsStringWithDepend(fieldMeta, domain);
            }
        } else if (fieldMeta.javaType() == BigInteger.class) {
            identifier = new BigInteger(nextAsStringWithDepend(fieldMeta, domain));
        } else {
            throw new IllegalArgumentException(String.format("SnowflakeGenerator unsupported java type[%s]"
                    , fieldMeta.javaType().getName()));
        }
        return identifier;
    }

    public final Snowflake getSnowflake() {
        return this.snowflake;
    }

    private String nextAsStringWithDepend(FieldMeta<?, ?> fieldMeta, ReadWrapper entityWrapper) {
        GeneratorMeta generatorMeta = fieldMeta.generator();
        _Assert.notNull(generatorMeta, "paramMeta must have generator");

        String dependOnProp = generatorMeta.dependFieldName();
        if (!StringUtils.hasText(dependOnProp)) {
            return snowflake.nextAsString();
        }
        _Assert.isTrue(entityWrapper.isReadable(dependOnProp)
                , () -> String.format("paramMeta[%s.%s] depend not readable"
                        , fieldMeta.javaType().getName(), fieldMeta.fieldName()));

        Object dependValue = entityWrapper.get(dependOnProp);

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
    private void registerGenerator(GenericSessionFactory sessionFactory) {
        SnowflakeClient client = getSnowflakeClient(sessionFactory);
        for (int i = 0; !client.registerGenerator(this); i++) {
            updateSnowflake();
            _Assert.state(i <= 10, "SnowflakeGenerator register count exception");
        }
    }

    /**
     * package instance method for {@link AbstractSnowflakeClient}
     */
    void updateSnowflake() {
        SnowflakeClient client = SNOWFLAKE_CLIENT.get();
        _Assert.state(client != null, "SnowflakeClient not init");

        final Snowflake oldSnowflake = snowflakeHolder.get();
        final Worker worker = client.currentWorker();

        if (isMatchWorker(worker, oldSnowflake)) {
            return;
        }

        final Method method = SNOWFLAKE_BUILDER.get();

        _Assert.state(method != null, "SnowflakeGenerator not init");
        Snowflake newSnowflake = (Snowflake) ReflectionUtils.invokeMethod(method, null
                , oldSnowflake.getStartTime(), worker);

        _Assert.state(newSnowflake != null, () -> String.format("method[%s] return null", method));
        snowflakeHolder.compareAndSet(oldSnowflake, newSnowflake);
        this.snowflake = snowflakeHolder.get();
    }


}
