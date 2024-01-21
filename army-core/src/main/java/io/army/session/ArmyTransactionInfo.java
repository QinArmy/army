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

package io.army.session;

import io.army.util._Collections;
import io.army.util._StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * <p>This class is package class.
 *
 * @since 0.6.0
 */
final class ArmyTransactionInfo implements TransactionInfo {


    static ArmyTransactionInfo create(final boolean inTransaction, final @Nullable Isolation isolation,
                                      final boolean readOnly) {
        if (isolation == null) {
            throw new NullPointerException("isolation must non-null");
        }
        final ArmyTransactionInfo info;
        if (inTransaction) {
            info = new ArmyTransactionInfo(true, isolation, readOnly);
        } else if (isolation == Isolation.REPEATABLE_READ) {
            info = readOnly ? REPEATABLE_READ_READ : REPEATABLE_READ_WRITE;
        } else if (isolation == Isolation.READ_COMMITTED) {
            info = readOnly ? READ_COMMITTED_READ : READ_COMMITTED_WRITE;
        } else if (isolation == Isolation.SERIALIZABLE) {
            info = readOnly ? SERIALIZABLE_READ : SERIALIZABLE_WRITE;
        } else {
            info = new ArmyTransactionInfo(false, isolation, readOnly);
        }
        return info;
    }


    static TransactionInfo forRollbackOnly(final TransactionInfo info) {
        if (!(info instanceof ArmyTransactionInfo)) {
            throw new IllegalArgumentException("unknown info implementation");
        }
        final ArmyTransactionInfo armyInfo = (ArmyTransactionInfo) info;
        if (!(armyInfo.inTransaction || armyInfo.isolation == Isolation.PSEUDO)) {
            throw new IllegalArgumentException("Illegal transaction info");
        }
        final Map<Option<?>, Object> map = _Collections.hashMap();
        for (Option<?> option : armyInfo.optionSet) {
            map.put(option, armyInfo.optionFunc.apply(option));
        }
        map.put(Option.ROLLBACK_ONLY, Boolean.TRUE);
        return new ArmyTransactionInfo(armyInfo.inTransaction, armyInfo.isolation, armyInfo.readOnly, map);
    }

    static InfoBuilder builder(boolean inTransaction, @Nullable Isolation isolation, boolean readOnly) {
        if (isolation == null) {
            throw new NullPointerException("isolation must non-null");
        } else if (isolation == Isolation.PSEUDO) {
            throw new IllegalArgumentException("PSEUDO couldn't be created by builder");
        }
        return new ArmyBuilder(inTransaction, isolation, readOnly);
    }


    static TransactionInfo pseudoLocal(final TransactionOption option) {
        final Map<Option<?>, Object> map = _Collections.hashMap();
        if (transactionStartMap(map, option) != Isolation.PSEUDO) {
            throw new IllegalArgumentException("Non-PSEUDO");
        } else if (!option.isReadOnly()) {
            throw pseudoMustReadonly();
        }
        return new ArmyTransactionInfo(false, Isolation.PSEUDO, true, map);
    }


    /**
     * <p>Create pseudo transaction info for XA transaction start method.
     */
    static TransactionInfo pseudoStart(final Xid xid, final int flags, final TransactionOption option) {

        final Map<Option<?>, Object> map = _Collections.hashMap();
        if (transactionStartMap(map, option) != Isolation.PSEUDO) {
            throw new IllegalArgumentException("Non-PSEUDO");
        } else if (!option.isReadOnly()) {
            throw pseudoMustReadonly();
        }
        map.put(Option.XID, xid);
        map.put(Option.XA_FLAGS, flags);
        map.put(Option.XA_STATES, XaStates.ACTIVE);
        return new ArmyTransactionInfo(false, Isolation.PSEUDO, true, map);
    }


    /**
     * <p>Create pseudo transaction info for XA transaction end method.
     */
    static TransactionInfo pseudoEnd(final TransactionInfo info, final int flags) {
        if (!(info instanceof ArmyTransactionInfo)) {
            throw new IllegalArgumentException("unknown info implementation");
        }
        final ArmyTransactionInfo armyInfo = (ArmyTransactionInfo) info;
        if (armyInfo.isolation != Isolation.PSEUDO || armyInfo.optionFunc.apply(Option.XA_STATES) != XaStates.ACTIVE) {
            throw new IllegalArgumentException("Non-PSEUDO");
        }

        final Map<Option<?>, Object> map = _Collections.hashMap();
        for (Option<?> option : armyInfo.optionSet) {
            map.put(option, armyInfo.optionFunc.apply(option));
        }

        map.put(Option.XA_FLAGS, flags);
        map.put(Option.XA_STATES, XaStates.IDLE);

        return new ArmyTransactionInfo(false, Isolation.PSEUDO, true, map);
    }

    /**
     * @see #pseudoLocal(TransactionOption)
     */
    @Nullable
    private static Isolation transactionStartMap(final Map<Option<?>, Object> map, final TransactionOption option) {
        map.put(Option.START_MILLIS, System.currentTimeMillis());

        final Integer timeoutMillis;
        timeoutMillis = option.valueOf(Option.TIMEOUT_MILLIS);
        if (timeoutMillis != null) {
            map.put(Option.TIMEOUT_MILLIS, timeoutMillis);
        }

        final Isolation isolation = option.isolation();
        map.put(Option.DEFAULT_ISOLATION, (isolation == null || isolation == Isolation.PSEUDO));

        final String name;
        name = option.valueOf(Option.NAME);
        if (name != null) {
            map.put(Option.NAME, name);
        }

        final String label;
        label = option.valueOf(Option.LABEL);
        if (label != null) {
            map.put(Option.LABEL, label);
        }

        return isolation;
    }

    private static IllegalArgumentException pseudoMustReadonly() {
        return new IllegalArgumentException("PSEUDO must read only.");
    }

    private static IllegalArgumentException pseudoMustNotInTransaction() {
        return new IllegalArgumentException("PSEUDO must not in transaction.");
    }


    private static final ArmyTransactionInfo READ_COMMITTED_WRITE = new ArmyTransactionInfo(false, Isolation.READ_COMMITTED, false);

    private static final ArmyTransactionInfo REPEATABLE_READ_WRITE = new ArmyTransactionInfo(false, Isolation.REPEATABLE_READ, false);

    private static final ArmyTransactionInfo SERIALIZABLE_WRITE = new ArmyTransactionInfo(false, Isolation.SERIALIZABLE, false);

    /*-------------------below read transaction option-------------------*/

    private static final ArmyTransactionInfo READ_COMMITTED_READ = new ArmyTransactionInfo(false, Isolation.READ_COMMITTED, true);

    private static final ArmyTransactionInfo REPEATABLE_READ_READ = new ArmyTransactionInfo(false, Isolation.REPEATABLE_READ, true);

    private static final ArmyTransactionInfo SERIALIZABLE_READ = new ArmyTransactionInfo(false, Isolation.SERIALIZABLE, true);


    private final boolean inTransaction;

    private final Isolation isolation;

    private final boolean readOnly;

    private final Function<Option<?>, ?> optionFunc;

    private final Set<Option<?>> optionSet;


    private ArmyTransactionInfo(boolean inTransaction, Isolation isolation, boolean readOnly) {
        this.inTransaction = inTransaction;
        this.isolation = isolation;
        this.readOnly = readOnly;
        this.optionFunc = Option.EMPTY_FUNC;
        this.optionSet = Collections.emptySet();
    }


    private ArmyTransactionInfo(boolean inTransaction, Isolation isolation, boolean readOnly, Map<Option<?>, ?> map) {
        this.inTransaction = inTransaction;
        this.isolation = isolation;
        this.readOnly = readOnly;

        if (map.size() == 0) {
            this.optionFunc = Option.EMPTY_FUNC;
            this.optionSet = Collections.emptySet();
        } else {
            this.optionFunc = map::get;
            this.optionSet = Collections.unmodifiableSet(map.keySet());
        }


    }


    @Override
    public boolean inTransaction() {
        return this.inTransaction;
    }

    @Override
    public boolean isRollbackOnly() {
        final Function<Option<?>, ?> optionFunc;

        final boolean rollbackOnly;
        if (!this.inTransaction && this.isolation != Isolation.PSEUDO) {
            rollbackOnly = false;
        } else if ((optionFunc = this.optionFunc) == Option.EMPTY_FUNC) {
            rollbackOnly = false;
        } else if (Boolean.TRUE.equals(optionFunc.apply(Option.ROLLBACK_ONLY))) {
            rollbackOnly = true;
        } else {
            final Object flags;
            rollbackOnly = (flags = optionFunc.apply(Option.XA_FLAGS)) instanceof Integer
                    && ((Integer) flags & RmSession.TM_FAIL) != 0;
        }
        return rollbackOnly;
    }

    @Nonnull
    @Override
    public Isolation isolation() {
        return this.isolation;
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(final @Nullable Option<T> option) {
        final Object value;
        if (option == null) {
            value = null;
        } else if (option == Option.IN_TRANSACTION) {
            value = this.inTransaction;
        } else if (option == Option.ISOLATION) {
            value = this.isolation;
        } else if (option == Option.READ_ONLY) {
            value = this.readOnly;
        } else if (this.optionFunc == Option.EMPTY_FUNC) {
            value = null;
        } else {
            value = this.optionFunc.apply(option);
        }
        if (option != null && option.javaType().isInstance(value)) {
            return (T) value;
        }
        return null;
    }


    @Override
    public Set<Option<?>> optionSet() {
        return this.optionSet;
    }

    @Override
    public String toString() {
        return _StringUtils.builder(88)
                .append(getClass().getName())
                .append("[name:")
                .append(valueOf(Option.NAME))
                .append(",inTransaction:")
                .append(this.inTransaction)
                .append(",isolation")
                .append(this.isolation.name())
                .append(",readOnly")
                .append(this.readOnly)
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append(",label:")
                .append(valueOf(Option.LABEL))
                .append(']')
                .toString();
    }


    private static final class ArmyBuilder implements InfoBuilder {

        private final boolean inTransaction;

        private final Isolation isolation;

        private final boolean readOnly;

        private Map<Option<?>, Object> optionMap;

        private ArmyBuilder(boolean inTransaction, Isolation isolation, boolean readOnly) {
            this.inTransaction = inTransaction;
            this.isolation = isolation;
            this.readOnly = readOnly;
        }

        @Override
        public <T> InfoBuilder option(final Option<T> option, final @Nullable T value) {
            Map<Option<?>, Object> optionMap = this.optionMap;
            if (optionMap == null && value == null) {
                return this;
            }
            if (optionMap == null) {
                this.optionMap = optionMap = _Collections.hashMap();
            }
            if (value == null) {
                optionMap.remove(option);
            } else {
                optionMap.put(option, value);
            }
            return this;
        }

        @Override
        public TransactionInfo build() {
            final Map<Option<?>, Object> optionMap = this.optionMap;

            final TransactionInfo info;
            if (optionMap == null || optionMap.size() == 0) {
                info = create(this.inTransaction, isolation, this.readOnly);
            } else {
                info = new ArmyTransactionInfo(this.inTransaction, this.isolation, this.readOnly, optionMap);
            }
            this.optionMap = null; // must immediately
            return info;
        }

    } // ArmyBuilder


}
