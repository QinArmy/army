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

package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.TypeDef;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect.impl._Constant;
import io.army.dialect.impl._DialectUtils;
import io.army.dialect.impl._SqlContext;
import io.army.meta.MetaException;
import io.army.sqltype.DataType;
import io.army.sqltype.SQLType;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.Locale;

public abstract class TypeDefs implements TypeDef {


    public static _TypeDefCharacterSetSpec precision(final DataType dataType, final boolean textType,
                                              final long precision, final long maxValue) {
        if (precision < 0L || precision > maxValue) {
            throw precisionError(dataType, precision);
        }
        return new TypeDefLength(dataType, textType, precision);
    }


    public static TypeDef precisionAndScale(final DataType dataType, final int precision, final int scale,
                                     final int maxPrecision, final int maxScale) {
        if (precision < 0 || precision > maxPrecision) {
            throw precisionError(dataType, precision);
        } else if (scale < 0 || scale > maxScale) {
            throw ContextStack.clearStackAndCriteriaError(String.format("%s scale[%s] error", dataType, precision));
        }
        return new TypeDefPrecisionAndScale(dataType, precision, scale);
    }


    private static CriteriaException precisionError(DataType type, long precision) {
        return ContextStack.clearStackAndCriteriaError(String.format("%s precision[%s] error", type, precision));
    }

    public final DataType dataType;

    private TypeDefs(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public final String typeName() {
        return this.dataType.typeName();
    }

    public static final class TypeDefLength extends TypeDefs implements _TypeDefCharacterSetSpec, _SelfDescribed {

        private final boolean textType;

        private final long length;

        private String charsetName;

        private String collationName;

        private TypeDefLength(DataType dataType, boolean textType, long length) {
            super(dataType);
            this.textType = textType;
            this.length = length;
        }

        @Override
        public TypeDef collate(final @Nullable String collationName) {
            if (!this.textType) {
                String m = String.format("%s don't support COLLATE clause.", this.dataType);
                throw ContextStack.clearStackAndCriteriaError(m);
            } else if (this.collationName != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (collationName == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.collationName = collationName;
            return this;
        }

        @Override
        public _TypeDefCollateClause characterSet(final @Nullable String charsetName) {
            if (!this.textType) {
                String m = String.format("%s don't support CHARACTER SET clause.", this.dataType);
                throw ContextStack.clearStackAndCriteriaError(m);
            } else if (this.charsetName != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (charsetName == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.charsetName = charsetName;
            return this;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE);

            final DataType type = this.dataType;
            final String typeName = type.typeName();
            final int spaceIndex;
            if (!(type instanceof SQLType)) {
                if (context.parser().isKeyWords(typeName.toUpperCase(Locale.ROOT))) {
                    throw new CriteriaException(String.format("typeName of %s is key word", type));
                } else if (_DialectUtils.isSimpleIdentifier(typeName)) {
                    sqlBuilder.append(typeName);
                } else {
                    context.identifier(typeName, sqlBuilder);
                }
                spaceIndex = -1;
            } else if (((SQLType) type).armyType().isUnsigned()) {
                spaceIndex = typeName.indexOf(" UNSIGNED");
                if (spaceIndex < 1) {
                    throw new MetaException(String.format("%s typeName error", type));
                }
                sqlBuilder.append(typeName, 0, spaceIndex);
            } else {
                sqlBuilder.append(typeName);
                spaceIndex = -1;
            }

            sqlBuilder.append(_Constant.LEFT_PAREN)
                    .append(this.length)
                    .append(_Constant.RIGHT_PAREN);

            if (spaceIndex > 1) {
                sqlBuilder.append(typeName, spaceIndex, typeName.length());
            }

            final String charsetName = this.charsetName, collationName = this.collationName;

            if (charsetName != null) {
                sqlBuilder.append(_Constant.SPACE_CHARACTER_SET_SPACE);
                context.identifier(charsetName, sqlBuilder);
            }

            if (collationName != null) {
                sqlBuilder.append(_Constant.SPACE_COLLATE_SPACE);
                context.identifier(collationName, sqlBuilder);
            }

        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(_Constant.SPACE);

            final DataType type = this.dataType;
            final String typeName = type.typeName();
            final int spaceIndex;
            if (!(type instanceof SQLType)) {
                builder.append(typeName);
                spaceIndex = -1;
            } else if (((SQLType) type).armyType().isUnsigned()) {
                spaceIndex = typeName.indexOf(" UNSIGNED");
                if (spaceIndex < 1) {
                    throw new MetaException(String.format("%s typeName error", type));
                }
                builder.append(typeName, 0, spaceIndex);
            } else {
                builder.append(typeName);
                spaceIndex = -1;
            }

            builder.append(_Constant.LEFT_PAREN)
                    .append(this.length)
                    .append(_Constant.RIGHT_PAREN);

            if (spaceIndex > 1) {
                builder.append(typeName, spaceIndex, typeName.length());
            }

            final String charsetName = this.charsetName, collationName = this.collationName;

            if (charsetName != null) {
                builder.append(_Constant.SPACE_CHARACTER_SET_SPACE)
                        .append(charsetName);
            }

            if (collationName != null) {
                builder.append(_Constant.SPACE_COLLATE_SPACE)
                        .append(collationName);
            }
            return builder.toString();
        }


    } // TypeDefLength


    private static final class TypeDefPrecisionAndScale extends TypeDefs implements _SelfDescribed {

        private final int precision;

        private final int scale;

        private TypeDefPrecisionAndScale(DataType dataType, int precision, int scale) {
            super(dataType);
            this.precision = precision;
            this.scale = scale;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE);

            final DataType type = this.dataType;
            final String typeName = type.typeName();
            final int spaceIndex;
            if (!(type instanceof SQLType)) {
                if (context.parser().isKeyWords(typeName.toUpperCase(Locale.ROOT))) {
                    throw new CriteriaException(String.format("typeName of %s is key word", type));
                } else if (_DialectUtils.isSimpleIdentifier(typeName)) {
                    sqlBuilder.append(typeName);
                } else {
                    context.identifier(typeName, sqlBuilder);
                }
                spaceIndex = -1;
            } else if (((SQLType) type).armyType().isUnsigned()) {
                spaceIndex = typeName.indexOf(" UNSIGNED");
                if (spaceIndex < 1) {
                    throw new MetaException(String.format("%s typeName error", type));
                }
                sqlBuilder.append(typeName, 0, spaceIndex);
            } else {
                sqlBuilder.append(typeName);
                spaceIndex = -1;
            }

            sqlBuilder.append(_Constant.LEFT_PAREN)
                    .append(this.precision)
                    .append(_Constant.COMMA)
                    .append(this.scale)
                    .append(_Constant.RIGHT_PAREN);

            if (spaceIndex > 1) {
                sqlBuilder.append(typeName, spaceIndex, typeName.length());
            }
        }


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(_Constant.SPACE);

            final DataType type = this.dataType;
            final String typeName = type.typeName();
            final int spaceIndex;
            if (!(type instanceof SQLType)) {
                builder.append(typeName);
                spaceIndex = -1;
            } else if (((SQLType) type).armyType().isUnsigned()) {
                spaceIndex = typeName.indexOf(" UNSIGNED");
                if (spaceIndex < 1) {
                    throw new MetaException(String.format("%s typeName error", type));
                }
                builder.append(typeName, 0, spaceIndex);
            } else {
                builder.append(typeName);
                spaceIndex = -1;
            }

            builder.append(_Constant.LEFT_PAREN)
                    .append(this.precision)
                    .append(_Constant.COMMA)
                    .append(this.scale)
                    .append(_Constant.RIGHT_PAREN);

            if (spaceIndex > 1) {
                builder.append(typeName, spaceIndex, typeName.length());
            }
            return builder.toString();
        }
    } // TypeDefPrecisionAndScale


}
