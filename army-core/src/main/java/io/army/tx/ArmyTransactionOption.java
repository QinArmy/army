package io.army.tx;

import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.session.Option;
import io.army.util._StringUtils;

/**
 * <p>package class
 *
 * @since 1.0
 */
final class ArmyTransactionOption implements TransactionInfo {


    private static final ArmyTransactionOption READ_UNCOMMITTED_READ = new ArmyTransactionOption(Isolation.READ_UNCOMMITTED, true);
    private static final ArmyTransactionOption READ_UNCOMMITTED_WRITE = new ArmyTransactionOption(Isolation.READ_UNCOMMITTED, false);

    private static final ArmyTransactionOption READ_COMMITTED_READ = new ArmyTransactionOption(Isolation.READ_COMMITTED, true);
    private static final ArmyTransactionOption READ_COMMITTED_WRITE = new ArmyTransactionOption(Isolation.READ_COMMITTED, false);

    private static final ArmyTransactionOption REPEATABLE_READ_READ = new ArmyTransactionOption(Isolation.REPEATABLE_READ, true);
    private static final ArmyTransactionOption REPEATABLE_READ_WRITE = new ArmyTransactionOption(Isolation.REPEATABLE_READ, false);

    private static final ArmyTransactionOption SERIALIZABLE_READ = new ArmyTransactionOption(Isolation.SERIALIZABLE, true);
    private static final ArmyTransactionOption SERIALIZABLE_WRITE = new ArmyTransactionOption(Isolation.SERIALIZABLE, false);


    private final Isolation isolation;

    private final boolean readOnly;

    private ArmyTransactionOption(Isolation isolation, boolean readOnly) {
        this.isolation = isolation;
        this.readOnly = readOnly;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(Option<T> option) {
        final Object value;
        if (option == Option.IN_TRANSACTION) {
            value = Boolean.FALSE;
        } else if (option == Option.ISOLATION) {
            value = this.isolation;
        } else if (option == Option.READ_ONLY) {
            value = this.readOnly;
        } else {
            value = null;
        }
        return (T) value;
    }

    @NonNull
    @Override
    public Isolation isolation() {
        return this.isolation;
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public boolean inTransaction() {
        // always false
        return false;
    }

    @Override
    public String toString() {
        return _StringUtils.builder(25)
                .append(getClass().getName())
                .append("[inTransaction:false,isolation:")
                .append(this.isolation.name())
                .append(",readOnly:")
                .append(this.readOnly)
                .append(']')
                .toString();
    }


    static TransactionOption option(final @Nullable Isolation isolation, final boolean readOnly) {
        final TransactionOption option;
        if (isolation == null) {
            option = readOnly ? DefaultLevelTransactionOption.READ : DefaultLevelTransactionOption.WRITE;
        } else if (isolation == Isolation.READ_COMMITTED) {
            option = readOnly ? READ_COMMITTED_READ : READ_COMMITTED_WRITE;
        } else if (isolation == Isolation.REPEATABLE_READ) {
            option = readOnly ? REPEATABLE_READ_READ : REPEATABLE_READ_WRITE;
        } else if (isolation == Isolation.SERIALIZABLE) {
            option = readOnly ? SERIALIZABLE_READ : SERIALIZABLE_WRITE;
        } else if (isolation == Isolation.READ_UNCOMMITTED) {
            option = readOnly ? READ_UNCOMMITTED_READ : READ_UNCOMMITTED_WRITE;
        } else {
            option = new ArmyTransactionOption(isolation, readOnly);
        }
        return option;
    }


    private enum DefaultLevelTransactionOption implements TransactionOption {
        READ(true),
        WRITE(false);

        private final boolean readOnly;

        DefaultLevelTransactionOption(boolean readOnly) {
            this.readOnly = readOnly;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <T> T valueOf(Option<T> option) {
            final Object value;
            if (option == Option.IN_TRANSACTION) {
                value = Boolean.FALSE;
            } else if (option == Option.ISOLATION) {
                value = null;
            } else if (option == Option.READ_ONLY) {
                value = this.readOnly;
            } else {
                value = null;
            }
            return (T) value;
        }

        @Override
        public final Isolation isolation() {
            // always null
            return null;
        }

        @Override
        public final boolean isReadOnly() {
            return this.readOnly;
        }

        @Override
        public final String toString() {
            return String.format("%s[inTransaction:false,isolation:null,readOnly:%s].",
                    getClass().getName(),
                    this.readOnly
            );
        }


    }//DefaultLevelTransactionOption


}
