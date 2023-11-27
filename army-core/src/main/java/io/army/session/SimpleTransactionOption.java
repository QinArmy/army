package io.army.session;

import io.army.util._Collections;
import io.army.util._StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

abstract class SimpleTransactionOption implements TransactionOption {


    static TransactionOption option(final @Nullable Isolation isolation, final boolean readOnly) {
        final TransactionOption option;
        if (isolation == null) {
            option = readOnly ? DefaultLevelTransactionOption.READ : DefaultLevelTransactionOption.WRITE;
        } else if (isolation == Isolation.READ_COMMITTED) {
            option = readOnly ? ArmyTransactionOption.READ_COMMITTED_READ : ArmyTransactionOption.READ_COMMITTED_WRITE;
        } else if (isolation == Isolation.REPEATABLE_READ) {
            option = readOnly ? ArmyTransactionOption.REPEATABLE_READ_READ : ArmyTransactionOption.REPEATABLE_READ_WRITE;
        } else if (isolation == Isolation.SERIALIZABLE) {
            option = readOnly ? ArmyTransactionOption.SERIALIZABLE_READ : ArmyTransactionOption.SERIALIZABLE_WRITE;
        } else {
            option = new ArmyTransactionOption(isolation, readOnly);
        }
        return option;
    }


    static Builder builder() {
        return new TransactionOptionBuilder();
    }


    final Isolation isolation;

    final boolean readOnly;

    private SimpleTransactionOption(Isolation isolation, boolean readOnly) {
        this.isolation = isolation;
        this.readOnly = readOnly;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T valueOf(ArmyOption<T> option) {
        final Object value;
        if (option == ArmyOption.IN_TRANSACTION) {
            value = Boolean.FALSE;
        } else if (option == ArmyOption.ISOLATION) {
            value = this.isolation;
        } else if (option == ArmyOption.READ_ONLY) {
            value = this.readOnly;
        } else {
            value = null;
        }
        return (T) value;
    }

    @Nonnull
    @Override
    public final Isolation isolation() {
        return this.isolation;
    }

    @Override
    public final boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public abstract String toString();


    /**
     * <p>This class is simple implementation of {@link TransactionInfo}.
     * <p>package class
     *
     * @since 1.0
     */
    static final class ArmyTransactionOption extends SimpleTransactionOption implements TransactionOption {


        private static final ArmyTransactionOption READ_COMMITTED_READ = new ArmyTransactionOption(Isolation.READ_COMMITTED, true);
        private static final ArmyTransactionOption READ_COMMITTED_WRITE = new ArmyTransactionOption(Isolation.READ_COMMITTED, false);

        private static final ArmyTransactionOption REPEATABLE_READ_READ = new ArmyTransactionOption(Isolation.REPEATABLE_READ, true);
        private static final ArmyTransactionOption REPEATABLE_READ_WRITE = new ArmyTransactionOption(Isolation.REPEATABLE_READ, false);

        private static final ArmyTransactionOption SERIALIZABLE_READ = new ArmyTransactionOption(Isolation.SERIALIZABLE, true);
        private static final ArmyTransactionOption SERIALIZABLE_WRITE = new ArmyTransactionOption(Isolation.SERIALIZABLE, false);


        private ArmyTransactionOption(Isolation isolation, boolean readOnly) {
            super(isolation, readOnly);
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


    }// ArmyTransactionOption

    private enum DefaultLevelTransactionOption implements TransactionOption {
        READ(true),
        WRITE(false);

        private final boolean readOnly;

        DefaultLevelTransactionOption(boolean readOnly) {
            this.readOnly = readOnly;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final <T> T valueOf(ArmyOption<T> option) {
            final Object value;
            if (option == ArmyOption.IN_TRANSACTION) {
                value = Boolean.FALSE;
            } else if (option == ArmyOption.ISOLATION) {
                value = null;
            } else if (option == ArmyOption.READ_ONLY) {
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

    private static final class TransactionOptionBuilder implements Builder {

        private Map<ArmyOption<?>, Object> optionMap;

        private TransactionOptionBuilder() {
            this.optionMap = _Collections.hashMap();
        }

        @Override
        public <T> Builder option(ArmyOption<T> option, @Nullable T value) {
            Map<ArmyOption<?>, Object> map = this.optionMap;
            if (map == null) {
                this.optionMap = map = _Collections.hashMap();
            }
            if (value == null) {
                this.optionMap.remove(option);
            } else {
                this.optionMap.put(option, value);
            }
            return this;
        }

        @Override
        public TransactionOption build() throws IllegalArgumentException {
            Map<ArmyOption<?>, Object> map = this.optionMap;
            if (map == null) {
                this.optionMap = map = _Collections.hashMap();
            }
            if (map.containsKey(ArmyOption.IN_TRANSACTION)) {
                throw new IllegalArgumentException("don't support IN_TRANSACTION option");
            }
            map.putIfAbsent(ArmyOption.READ_ONLY, Boolean.FALSE);
            final DialectTransactionOption option;
            option = new DialectTransactionOption(map::get);

            this.optionMap = null; // clear
            return option;
        }

    }// TransactionOptionBuilder


    private static final class DialectTransactionOption implements TransactionOption {

        private final Function<ArmyOption<?>, ?> optionFunc;

        private DialectTransactionOption(Function<ArmyOption<?>, ?> optionFunc) {
            this.optionFunc = optionFunc;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T valueOf(ArmyOption<T> option) {
            return (T) this.optionFunc.apply(option);
        }

        @Override
        public Isolation isolation() {
            return valueOf(ArmyOption.ISOLATION);
        }

        @Override
        public boolean isReadOnly() {
            return nonNullOf(ArmyOption.READ_ONLY);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(getClass().getName())
                    .append("[isolation:");

            final Isolation isolation = this.isolation();
            if (isolation == null) {
                builder.append("null");
            } else {
                builder.append(isolation.name());
            }
            return builder.append(",readOnly:")
                    .append(this.isReadOnly())
                    .append(']')
                    .toString();
        }


    }// DialectTransactionOption

    private static final class ArmyTransactionInfo extends SimpleTransactionOption implements TransactionOption {

        private final boolean inTransaction;


        private ArmyTransactionInfo(boolean inTransaction, Isolation isolation, boolean readOnly) {
            super(isolation, readOnly);
            this.inTransaction = inTransaction;
        }


        @Override
        public String toString() {
            return _StringUtils.builder(30)
                    .append(getClass().getName())
                    .append("[inTransaction:")
                    .append(this.inTransaction)
                    .append(",isolation")
                    .append(this.isolation.name())
                    .append(",readOnly:")
                    .append(this.readOnly)
                    .append(']')
                    .toString();
        }


    }// ArmyTransactionInfo

    private static final class DialectTransactionInfo implements TransactionOption {

        private final Function<ArmyOption<?>, ?> optionFunc;

        private DialectTransactionInfo(Function<ArmyOption<?>, ?> optionFunc) {
            this.optionFunc = optionFunc;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T valueOf(ArmyOption<T> option) {
            return (T) this.optionFunc.apply(option);
        }

        @Nonnull
        @Override
        public Isolation isolation() {
            return nonNullOf(ArmyOption.ISOLATION);
        }

        @Override
        public boolean isReadOnly() {
            return nonNullOf(ArmyOption.READ_ONLY);
        }

        @Override
        public String toString() {
            return _StringUtils.builder(30)
                    .append(getClass().getName())
                    .append("[isolation:")
                    .append(this.isolation().name())
                    .append(",readOnly:")
                    .append(this.isReadOnly())
                    .append(']')
                    .toString();
        }


    }// DialectTransactionInfo


}
