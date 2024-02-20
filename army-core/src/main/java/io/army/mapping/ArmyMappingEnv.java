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

package io.army.mapping;

import io.army.codec.JsonCodec;
import io.army.codec.XmlCodec;
import io.army.dialect.LiteralBinder;
import io.army.meta.ServerMeta;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.time.Clock;
import java.time.ZoneOffset;

final class ArmyMappingEnv implements MappingEnv {

    static Builder builder() {
        return new EnvBuilder();
    }

    private final boolean reactive;

    private final ServerMeta serverMeta;

    private final ZoneOffset zoneOffset;

    private final LiteralBinder literalBinder;

    private final JsonCodec jsonCodec;

    private final XmlCodec xmlCodec;


    private ArmyMappingEnv(EnvBuilder builder) {
        this.reactive = builder.reactive;
        this.serverMeta = builder.serverMeta;
        this.zoneOffset = builder.zoneOffset;
        this.literalBinder = builder.literalBinder;
        this.jsonCodec = builder.jsonCodec;
        this.xmlCodec = builder.xmlCodec;
        if (this.serverMeta == null) {
            throw new IllegalArgumentException("serverMeta must non-null");
        } else if (this.literalBinder == null) {
            throw new IllegalArgumentException("literalBinder must non-null");
        }
    }

    @Override
    public boolean isReactive() {
        return this.reactive;
    }

    @Override
    public ServerMeta serverMeta() {
        return this.serverMeta;
    }

    @Override
    public ZoneOffset zoneOffset() {
        ZoneOffset zoneId = this.zoneOffset;
        if (zoneId == null) {
            final Clock clock;
            clock = Clock.systemDefaultZone();
            zoneId = clock.getZone().getRules().getOffset(clock.instant());
        }
        return zoneId;
    }

    @Override
    public LiteralBinder literalBinder() {
        return this.literalBinder;
    }

    @Override
    public JsonCodec jsonCodec() {
        final JsonCodec codec = this.jsonCodec;
        if (codec == null) {
            throw new IllegalStateException("don't support JsonCodec");
        }
        return codec;
    }

    @Override
    public XmlCodec xmlCodec() {
        final XmlCodec codec = this.xmlCodec;
        if (codec == null) {
            throw new IllegalStateException("don't support XmlCodec");
        }
        return codec;
    }

    @Override
    public String toString() {
        return _StringUtils.builder(60)
                .append(getClass().getName())
                .append("[reactive:")
                .append(this.reactive)
                .append(",serverMeta:")
                .append(this.serverMeta)
                .append(",zoneId:")
                .append(this.zoneOffset)
                .append(",jsonCodec:")
                .append(this.jsonCodec)
                .append(",xmlCodec:")
                .append(this.xmlCodec)
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append(']')
                .toString();
    }

    private static final class EnvBuilder implements Builder {

        private boolean reactive;

        private ServerMeta serverMeta;

        private ZoneOffset zoneOffset;

        private LiteralBinder literalBinder;

        private JsonCodec jsonCodec;

        private XmlCodec xmlCodec;

        @Override
        public Builder reactive(boolean yes) {
            this.reactive = yes;
            return this;
        }

        @Override
        public Builder serverMeta(ServerMeta meta) {
            this.serverMeta = meta;
            return this;
        }

        @Override
        public Builder zoneOffset(@Nullable ZoneOffset zoneOffset) {
            this.zoneOffset = zoneOffset;
            return this;
        }

        @Override
        public Builder literalBinder(LiteralBinder literalBinder) {
            this.literalBinder = literalBinder;
            return this;
        }

        @Override
        public Builder jsonCodec(@Nullable JsonCodec codec) {
            this.jsonCodec = codec;
            return this;
        }

        @Override
        public Builder xmlCodec(@Nullable XmlCodec codec) {
            this.xmlCodec = codec;
            return this;
        }

        @Override
        public MappingEnv build() {
            return new ArmyMappingEnv(this);
        }

    } // BuilderImpl


}
