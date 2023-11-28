package io.army.session;

import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * <p>This class is package class.
 *
 * @since 1.0
 */
final class ArmyTransactionInfo implements TransactionInfo {

    static ArmyTransactionInfo create(final boolean inTransaction, final @Nullable Isolation isolation,
                                      final boolean readOnly, final @Nullable Function<Option<?>, ?> optionFunc) {
        if (isolation == null || optionFunc == null) {
            throw new NullPointerException();
        }
        final XaStates states;
        states = (XaStates) optionFunc.apply(Option.XA_STATES);
        if (states != null) {
            switch (states) {
                case ACTIVE:
                case IDLE: {
                    if (!inTransaction) {
                        throw new IllegalArgumentException("inTransaction error");
                    }
                }
                break;
                case PREPARED: {
                    if (inTransaction) {
                        throw new IllegalArgumentException("inTransaction error");
                    }
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(states);
            }
        }

        if (inTransaction == (isolation == Isolation.PSEUDO)) {
            String m = String.format("inTransaction[%s] and Isolation[%s] not match.", inTransaction, isolation.name());
            throw new IllegalArgumentException(m);
        } else if (readOnly ^ (isolation == Isolation.PSEUDO)) {
            String m = String.format("readOnly[%s] and Isolation[%s] not match.", readOnly, isolation.name());
            throw new IllegalArgumentException(m);
        } else if (optionFunc != Option.EMPTY_OPTION_FUNC) {
            if (optionFunc.apply(Option.TIMEOUT_MILLIS) != null && optionFunc.apply(Option.START_MILLIS) == null) {
                String m = String.format("Option[%s] and Option[%s] not match.", Option.TIMEOUT_MILLIS.name(),
                        Option.START_MILLIS.name());
                throw new IllegalArgumentException(m);
            }
        }
        return new ArmyTransactionInfo(inTransaction, isolation, readOnly, optionFunc);
    }

    private final boolean inTransaction;

    private final Isolation isolation;

    private final boolean readOnly;

    private final Function<Option<?>, ?> optionFunc;


    private ArmyTransactionInfo(boolean inTransaction, Isolation isolation, boolean readOnly,
                                Function<Option<?>, ?> optionFunc) {
        this.inTransaction = inTransaction;
        this.isolation = isolation;
        this.readOnly = readOnly;
        this.optionFunc = optionFunc;
    }


    @Override
    public boolean inTransaction() {
        return this.inTransaction;
    }

    @Override
    public boolean isRollbackOnly() {
        final Function<Option<?>, ?> optionFunc;

        final boolean rollbackOnly;
        if (this.isolation == Isolation.PSEUDO) {
            optionFunc = this.optionFunc;
            rollbackOnly = optionFunc != Option.EMPTY_OPTION_FUNC
                    && Boolean.TRUE.equals(optionFunc.apply(Option.ROLLBACK_ONLY));
        } else if (!this.inTransaction) {
            rollbackOnly = false;
        } else if ((optionFunc = this.optionFunc) == Option.EMPTY_OPTION_FUNC) {
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
        } else if (this.optionFunc == Option.EMPTY_OPTION_FUNC) {
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


}
