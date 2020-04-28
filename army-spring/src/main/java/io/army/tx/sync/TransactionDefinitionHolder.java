package io.army.tx.sync;


import io.army.datasource.PrimarySecondaryRoutingDataSource;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * transaction definition holder
 *
 * @see TransactionDefinitionInterceptor
 * @see PrimarySecondaryRoutingDataSource
 */
public abstract class TransactionDefinitionHolder {

    private static final ThreadLocal<TxDefinitionHolder> HOLDER = new NamedThreadLocal<>("transaction definition holder");

    private static final Map<Integer, Isolation> ISOLATION_MAP = initIsolationMap();

    private static final Map<Integer, Propagation> PROPAGATION_MAP = initPropagationMap();


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

        private TxDefinitionHolder(@Nullable TxDefinitionHolder suspended, @NonNull TransactionAttribute definition,
                                   @NonNull String name) {
            this.suspended = suspended;
            this.definition = definition;
            this.name = name;
            Assert.notNull(definition, "definition required");
        }
    }


    static void push(@NonNull TransactionAttribute definition, @NonNull Method method) {
        String txName = StringUtils.hasText(definition.getName()) ? definition.getName() : method.toString();
        HOLDER.set(new TxDefinitionHolder(HOLDER.get(), definition, txName));
    }

    static void pop() {
        TxDefinitionHolder current = HOLDER.get();
        if (current != null) {
            if (current.suspended == null) {
                HOLDER.remove();
            } else {
                HOLDER.set(current.suspended);
            }
        } else {
            throw new IllegalStateException("current transaction definition holder error.");
        }
    }


    @Nullable
    public static TransactionAttribute get() {
        TxDefinitionHolder holder = HOLDER.get();

        TransactionAttribute attribute = null;
        if (holder != null
                && isOuterDef(holder.definition.getPropagationBehavior())) {
            attribute = holder.definition;

        }
        return attribute;
    }


    public static boolean isReadOnly() {
        TransactionDefinition transactionDefinition = get();
        return transactionDefinition == null || transactionDefinition.isReadOnly();
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
        if (transactionDefinition != null) {
            level = ISOLATION_MAP.get(transactionDefinition.getIsolationLevel());
        } else {
            level = null;
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
        if (transactionDefinition != null) {
            propagation = PROPAGATION_MAP.get(transactionDefinition.getPropagationBehavior());
        } else {
            propagation = null;
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
        if (holder != null) {
            name = holder.name;
        } else {
            name = null;
        }
        return name;
    }

    /*################################## blow private method ##################################*/

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

    /**
     * note {@link TransactionDefinition#PROPAGATION_NOT_SUPPORTED} 虽可被 push 到 holder 中,
     * 但它在本类中不是外层事务定义,因为它以无事务执行.
     *
     * @return true 传播行为是外层事务定义
     */
    private static boolean isOuterDef(int def) {
        return def == TransactionDefinition.PROPAGATION_REQUIRED
                || def == TransactionDefinition.PROPAGATION_REQUIRES_NEW
                || def == TransactionDefinition.PROPAGATION_NESTED;
    }


}
