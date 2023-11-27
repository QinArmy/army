package io.army.tx.sync;


import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Transaction definition holder for read-write splitting .
 *
 * @see TransactionDefinitionInterceptor
 * @see io.army.datasource.sync.PrimarySecondaryRoutingDataSource
 * @see io.army.datasource.sync.PrimarySecondaryRoutingXADataSource
 * @see org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
 * @since 1.0
 */
public abstract class TransactionDefinitionHolder {

    private static final ThreadLocal<TxDefinitionHolder> HOLDER = new NamedThreadLocal<>(
            "transaction definition holder");

    private static final Map<Integer, Isolation> ISOLATION_MAP = initIsolationMap();

    private static final Map<Integer, Propagation> PROPAGATION_MAP = initPropagationMap();

    private static final ConcurrentMap<String, Boolean> TX_MANAGER_USE_SAVE_POINT_HOLDER = new ConcurrentHashMap<>();

    /**
     * transaction definition link node.
     */
    private static class TxDefinitionHolder {

        /**
         * suspended transaction
         */
        private final TxDefinitionHolder suspended;

        /**
         * current transaction definition
         */
        private final TransactionAttribute definition;

        /**
         * current transaction name
         */
        private final String name;

        private TxDefinitionHolder(@Nullable TxDefinitionHolder suspended, @Nonnull TransactionAttribute definition,
                                   @Nonnull String name) {
            this.suspended = suspended;
            this.definition = definition;
            this.name = name;
            Assert.notNull(definition, "definition required");
        }
    }


    /**
     * This method classically is used by {@link TransactionDefinitionInterceptor#invoke(MethodInvocation)} .
     *
     * @param txManagerName {@link Transactional#value()} or {@link ArmySyncLocalTransactionManager#setBeanName(String)}
     * @return true:use save points for nested transaction.
     * @throws IllegalArgumentException throw when txManagerName not register
     * @see #registerTransactionManager(String, boolean)
     * @see TransactionDefinitionInterceptor#invoke(MethodInvocation)
     */
    static boolean useSavepointForNested(String txManagerName) throws IllegalArgumentException {
        Boolean use = TX_MANAGER_USE_SAVE_POINT_HOLDER.get(txManagerName);
        if (use == null) {
            throw new IllegalArgumentException(String.format("Transaction manager[%s] not register.", txManagerName));
        }
        return use;
    }

    /**
     * @param txManagerBeanName transaction manager bean name
     * @throws IllegalStateException throw when if useSavepointForNestedTransaction is different from previous value.
     */
    static void registerTransactionManager(String txManagerBeanName, boolean useSavepointForNestedTransaction)
            throws IllegalStateException {
        Boolean use;
        use = TX_MANAGER_USE_SAVE_POINT_HOLDER.putIfAbsent(txManagerBeanName, useSavepointForNestedTransaction);
        if (use != null && use != useSavepointForNestedTransaction) {
            throw new IllegalStateException(String.format(
                    "Transaction manager[%s] previous value[%s] and expected value[%s] not match."
                    , txManagerBeanName, use, useSavepointForNestedTransaction));
        }
    }


    static void push(TransactionAttribute definition, Method method) {
        String txManagerName = definition.getQualifier();
        if (txManagerName == null) {
            throw new NoTransactionManagerNameException(method.toString());
        }
        if (!supportPropagation(txManagerName, definition.getPropagationBehavior())) {
            throw new IllegalArgumentException(String.format("Propagation[%s] not support"
                    , PROPAGATION_MAP.get(definition.getPropagationBehavior())));
        }
        String txName = StringUtils.hasText(definition.getName()) ? definition.getName() : method.toString();

        HOLDER.set(new TxDefinitionHolder(HOLDER.get(), definition, txName));
    }

    static void pop() throws IllegalStateException {
        TxDefinitionHolder current = HOLDER.get();
        if (current != null) {
            HOLDER.remove();
            if (current.suspended != null) {
                HOLDER.set(current.suspended);
            }
        } else {
            throw new IllegalStateException("transaction definition holder is empty.");
        }
    }


    public static boolean isReadOnly() {
        TransactionDefinition definition = get();
        return definition == null || definition.isReadOnly();
    }

    /**
     * Isolation of transaction
     *
     * @return null or {@link Isolation}
     */
    @Nullable
    public static Isolation getIsolation() {
        TransactionDefinition transactionDefinition = get();
        Isolation level;
        if (transactionDefinition == null) {
            level = null;
        } else {
            level = ISOLATION_MAP.get(transactionDefinition.getIsolationLevel());
        }
        return level;
    }

    /**
     * @return null or {@link Propagation}
     */
    @Nullable
    public static Propagation getPropagation() {
        TransactionDefinition transactionDefinition = get();
        Propagation propagation;
        if (transactionDefinition == null) {
            propagation = null;
        } else {
            propagation = PROPAGATION_MAP.get(transactionDefinition.getPropagationBehavior());
        }
        return propagation;
    }


    public static int getTimeout() {
        TransactionDefinition transactionDefinition = get();
        return transactionDefinition == null
                ? TransactionDefinition.TIMEOUT_DEFAULT
                : transactionDefinition.getTimeout();
    }

    /**
     * @return name ({@link TransactionDefinition}} or {@link Method#toString()} ) of transaction
     */
    @Nullable
    public static String getName() {
        TxDefinitionHolder holder = HOLDER.get();
        String name;
        if (holder == null) {
            name = null;
        } else {
            name = holder.name;
        }
        return name;
    }

    /*################################## blow private method ##################################*/

    @Nullable
    private static TransactionDefinition get() {
        TxDefinitionHolder holder = HOLDER.get();

        TransactionAttribute definition = null;
        if (holder != null) {

            definition = holder.definition;

        }
        return definition;
    }

    private static Map<Integer, Isolation> initIsolationMap() {
        Map<Integer, Isolation> map = new HashMap<>(10);
        for (Isolation value : Isolation.values()) {
            map.put(value.value(), value);
        }
        return Collections.unmodifiableMap(map);
    }

    private static Map<Integer, Propagation> initPropagationMap() {
        Map<Integer, Propagation> map = new HashMap<>(10);
        for (Propagation value : Propagation.values()) {
            map.put(value.value(), value);
        }
        return Collections.unmodifiableMap(map);
    }

    private static boolean supportPropagation(String txManagerName, int propagation) {
        return propagation == TransactionDefinition.PROPAGATION_REQUIRED
                || propagation == TransactionDefinition.PROPAGATION_REQUIRES_NEW
                || propagation == TransactionDefinition.PROPAGATION_NOT_SUPPORTED
                || (propagation == TransactionDefinition.PROPAGATION_NESTED && supportNested(txManagerName))
                ;
    }

    private static boolean supportNested(String txManagerName) {
        boolean support;
        if (isActiveForCurrent()) {
            support = !useSavepointForNested(txManagerName);
        } else {
            support = true;
        }
        return support;
    }

    private static boolean isActiveForCurrent() {
        TransactionDefinition definition = get();
        if (definition == null) {
            return false;
        }
        int propagation = definition.getPropagationBehavior();
        return propagation == TransactionDefinition.PROPAGATION_REQUIRED
                || propagation == TransactionDefinition.PROPAGATION_REQUIRES_NEW
                // If current is PROPAGATION_NESTED than transaction is active.
                || propagation == TransactionDefinition.PROPAGATION_NESTED;
    }

    static class NoTransactionManagerNameException extends RuntimeException {

        NoTransactionManagerNameException(String methodName) {
            super(String.format(
                    "Method[%s] no specified %s.value()"
                    , methodName, Transactional.class.getName()));
        }
    }


}
