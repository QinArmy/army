package io.army.session;

import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

/**
 * <p>This class is package class.
 *
 * @since 1.0
 */
final class ArmyXid implements Xid {


    static ArmyXid from(final String gtrid, final @Nullable String bqual, final int formatId,
                        @Nullable Function<Option<?>, ?> optionFunc) {
        if (!_StringUtils.hasText(gtrid)) {
            throw new IllegalArgumentException("gtrid must have text");
        } else if (bqual != null && !_StringUtils.hasText(gtrid)) {
            throw new IllegalArgumentException("bqual must be null or  have text");
        } else if (optionFunc == null) {
            throw new NullPointerException();
        }
        return new ArmyXid(gtrid, bqual, formatId, optionFunc);
    }

    private final String gtrid;

    private final String bqual;

    private final int formatId;

    private final Function<Option<?>, ?> optionFunc;

    private ArmyXid(String gtrid, @Nullable String bqual, int formatId, Function<Option<?>, ?> optionFunc) {
        this.gtrid = gtrid;
        this.bqual = bqual;
        this.formatId = formatId;
        this.optionFunc = optionFunc;
    }

    @Override
    public String getGtrid() {
        return this.gtrid;
    }

    @Override
    public String getBqual() {
        return this.bqual;
    }

    @Override
    public int getFormatId() {
        return this.formatId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(final @Nullable Option<T> option) {
        if (option == null) {
            return null;
        }
        final Object value;
        value = this.optionFunc.apply(option);
        if (option.javaType().isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    @Override
    public <T> T nonNullOf(Option<T> option) {
        throw new NullPointerException();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.gtrid, this.bqual, this.formatId);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof ArmyXid) {
            final ArmyXid o = (ArmyXid) obj;
            match = o.gtrid.equals(this.gtrid)
                    && Objects.equals(o.bqual, this.bqual)
                    && o.formatId == this.formatId;
        } else {
            match = false;
        }
        return match;
    }


    @Override
    public String toString() {
        return _StringUtils.builder()
                .append(getClass().getName())
                .append("[gtrid:")
                .append(this.gtrid)
                .append(",bqual:")
                .append(this.bqual)
                .append(",formatId:")
                .append(this.formatId)
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append(']')
                .toString();
    }


}
