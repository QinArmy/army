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

import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

/**
 * <p>This class is package class.
 *
 * @since 0.6.0
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

    /**
     * private constructor
     */
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
            throw new NullPointerException("option");
        }
        final Function<Option<?>, ?> function = this.optionFunc;
        Object value;
        if (function == Option.EMPTY_FUNC) {
            value = null;
        } else {
            value = function.apply(option);
            if (!option.javaType().isInstance(value)) {
                value = null;
            }
        }
        return (T) value;
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
        } else if (obj instanceof Xid o) {
            match = this.gtrid.equals(o.getGtrid())
                    && Objects.equals(o.getBqual(), this.bqual)
                    && o.getFormatId() == this.formatId;
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
