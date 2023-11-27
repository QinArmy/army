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

    static ArmyTransactionInfo create(boolean inTransaction, @Nullable Isolation isolation, boolean readOnly,
                                      @Nullable Function<ArmyOption<?>, ?> optionFunc) {
        if (isolation == null || optionFunc == null) {
            throw new NullPointerException();
        }
        final XaStates states;
        states = (XaStates) optionFunc.apply(ArmyOption.XA_STATES);
        if (states != null) {
            switch (states) {
                case ACTIVE:
                case IDLE:
                    assert inTransaction;
                    break;
                case PREPARED:
                    assert !inTransaction;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(states);
            }
        }
        return new ArmyTransactionInfo(inTransaction, isolation, readOnly, optionFunc);
    }

    private final boolean inTransaction;

    private final Isolation isolation;

    private final boolean readOnly;

    private final Function<ArmyOption<?>, ?> optionFunc;


    private ArmyTransactionInfo(boolean inTransaction, Isolation isolation, boolean readOnly,
                                Function<ArmyOption<?>, ?> optionFunc) {
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
        final Function<ArmyOption<?>, ?> optionFunc;

        final boolean rollbackOnly;
        if (!this.inTransaction) {
            rollbackOnly = false;
        } else if ((optionFunc = this.optionFunc) == ArmyOption.EMPTY_OPTION_FUNC) {
            rollbackOnly = false;
        } else if (Boolean.TRUE.equals(optionFunc.apply(ArmyOption.ROLLBACK_ONLY))) {
            rollbackOnly = true;
        } else {
            final Object flags;
            rollbackOnly = (flags = optionFunc.apply(ArmyOption.XA_FLAGS)) instanceof Integer
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
    public <T> T valueOf(final @Nullable ArmyOption<T> option) {
        final Object value;
        if (option == null) {
            value = null;
        } else if (option == ArmyOption.IN_TRANSACTION) {
            value = this.inTransaction;
        } else if (option == ArmyOption.ISOLATION) {
            value = this.isolation;
        } else if (option == ArmyOption.READ_ONLY) {
            value = this.readOnly;
        } else if (this.optionFunc == ArmyOption.EMPTY_OPTION_FUNC) {
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
        return _StringUtils.builder()
                .append(getClass().getName())
                .append("[inTransaction:")
                .append(this.inTransaction)
                .append(",isolation")
                .append(this.isolation.name())
                .append(",readOnly")
                .append(this.readOnly)
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append(']')
                .toString();
    }


}
