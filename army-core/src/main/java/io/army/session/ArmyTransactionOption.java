package io.army.session;

import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

final class ArmyTransactionOption implements TransactionOption {


    static TransactionOption option(final @Nullable Isolation isolation, final boolean readOnly) {
        final TransactionOption option;
        if (isolation == null) {
            option = readOnly ? DEFAULT_READ : DEFAULT_WRITE;
        } else if (isolation == Isolation.REPEATABLE_READ) {
            option = readOnly ? REPEATABLE_READ_READ : REPEATABLE_READ_WRITE;
        } else if (isolation == Isolation.READ_COMMITTED) {
            option = readOnly ? READ_COMMITTED_READ : READ_COMMITTED_WRITE;
        } else if (isolation == Isolation.SERIALIZABLE) {
            option = readOnly ? SERIALIZABLE_READ : SERIALIZABLE_WRITE;
        } else if (isolation != Isolation.PSEUDO) {
            option = new ArmyTransactionOption(isolation, readOnly, Option.EMPTY_FUNC);
        } else if (readOnly) {
            option = PSEUDO;
        } else {
            throw new IllegalArgumentException("pseudo transaction must be readonly.");
        }
        return option;
    }


    static Builder builder() {
        return new TransactionOptionBuilder();
    }

    private static final ArmyTransactionOption DEFAULT_WRITE = new ArmyTransactionOption(null, false);

    private static final ArmyTransactionOption READ_COMMITTED_WRITE = new ArmyTransactionOption(Isolation.READ_COMMITTED, false);

    private static final ArmyTransactionOption REPEATABLE_READ_WRITE = new ArmyTransactionOption(Isolation.REPEATABLE_READ, false);

    private static final ArmyTransactionOption SERIALIZABLE_WRITE = new ArmyTransactionOption(Isolation.SERIALIZABLE, false);

    /*-------------------below read transaction option-------------------*/

    private static final ArmyTransactionOption DEFAULT_READ = new ArmyTransactionOption(null, true);

    private static final ArmyTransactionOption READ_COMMITTED_READ = new ArmyTransactionOption(Isolation.READ_COMMITTED, true);

    private static final ArmyTransactionOption REPEATABLE_READ_READ = new ArmyTransactionOption(Isolation.REPEATABLE_READ, true);

    private static final ArmyTransactionOption SERIALIZABLE_READ = new ArmyTransactionOption(Isolation.SERIALIZABLE, true);

    private static final ArmyTransactionOption PSEUDO = new ArmyTransactionOption(Isolation.PSEUDO, true);


    final Isolation isolation;

    final boolean readOnly;

    private final Function<Option<?>, ?> optionFunc;

    /**
     * private constructor
     */
    private ArmyTransactionOption(@Nullable Isolation isolation, boolean readOnly) {
        this(isolation, readOnly, Option.EMPTY_FUNC);
    }

    /**
     * private constructor
     */
    private ArmyTransactionOption(@Nullable Isolation isolation, boolean readOnly, Function<Option<?>, ?> optionFunc) {
        this.isolation = isolation;
        this.readOnly = readOnly;
        this.optionFunc = optionFunc;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(final Option<T> option) {
        final Function<Option<?>, ?> func;

        final Object value, temp;
        if (option == Option.ISOLATION) {
            value = this.isolation;
        } else if (option == Option.READ_ONLY) {
            value = this.readOnly;
        } else if (option == Option.IN_TRANSACTION) {
            value = Boolean.FALSE;
        } else if ((func = this.optionFunc) == Option.EMPTY_FUNC) {
            value = null;
        } else if ((temp = func.apply(option)) == null) {
            value = null;
        } else if (option.javaType().isInstance(temp)) {
            value = temp;
        } else {
            value = null;
        }
        return (T) value;
    }


    @Override
    public Isolation isolation() {
        return this.isolation;
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(46);

        builder.append(getClass().getName())
                .append("[isolation:");


        if (this.isolation == null) {
            builder.append("null");
        } else {
            builder.append(this.isolation.name());
        }

        builder.append(",readOnly:")
                .append(this.readOnly);


        String text;
        text = valueOf(Option.NAME);
        if (text != null) {
            builder.append(",name:")
                    .append(text);
        }

        text = valueOf(Option.LABEL);
        if (text != null) {
            builder.append(",label:")
                    .append(text);
        }
        return builder.append(",hash:")
                .append(System.identityHashCode(this))
                .append(']')
                .toString();
    }


    private static final class TransactionOptionBuilder implements Builder {

        private Map<Option<?>, Object> optionMap;

        private TransactionOptionBuilder() {
        }

        @Override
        public <T> Builder option(final Option<T> option, final @Nullable T value) {
            Map<Option<?>, Object> map = this.optionMap;
            if (value == null && map == null) {
                return this;
            }

            if (map == null) {
                this.optionMap = map = _Collections.hashMap();
            }
            if (value == null) {
                map.remove(option);
            } else {
                map.put(option, value);
            }
            return this;
        }

        @Override
        public TransactionOption build() throws IllegalArgumentException {
            final Map<Option<?>, Object> map = this.optionMap;
            if (map == null) {
                return DEFAULT_WRITE;
            }

            this.optionMap = null; // clear
            if (map.containsKey(Option.IN_TRANSACTION)) {
                throw new IllegalArgumentException("don't support IN_TRANSACTION option");
            }

            final Isolation isolation;
            isolation = (Isolation) map.remove(Option.ISOLATION);

            final boolean readOnly;
            readOnly = (Boolean) map.getOrDefault(Option.READ_ONLY, Boolean.FALSE);
            map.remove(Option.READ_ONLY);

            final TransactionOption option;
            if (map.size() == 0) {
                option = ArmyTransactionOption.option(isolation, readOnly);
            } else {
                option = new ArmyTransactionOption(isolation, readOnly, map::get);
            }
            return option;
        }

    } // TransactionOptionBuilder


}
