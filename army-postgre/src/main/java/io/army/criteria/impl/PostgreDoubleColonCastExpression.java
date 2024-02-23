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
import io.army.criteria.LiteralExpression;
import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.dialect._DialectUtils;
import io.army.dialect._SqlContext;
import io.army.env.EscapeMode;
import io.army.mapping.TextType;
import io.army.mapping.optional.NoCastTextType;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

/**
 * <p>
 * This class representing PostgreSQL-style typecast expression
 *
 * @since 0.6.0
 */
final class PostgreDoubleColonCastExpression extends OperationExpression.OperationDefiniteExpression
        implements LiteralExpression {


    static PostgreDoubleColonCastExpression cast(final String literal, final String typeName) {
        return new PostgreDoubleColonCastExpression(literal, typeName);
    }

    private final String literal;

    private final String typeName;


    private PostgreDoubleColonCastExpression(String literal, String typeName) {
        this.literal = literal;
        this.typeName = typeName;
    }

    @Override
    public TypeMeta typeMeta() {
        return NoCastTextType.INSTANCE;
    }

    @Override
    public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
        if (context.database() != Database.PostgreSQL) {
            throw new CriteriaException(String.format("dialect database isn't %s", Database.PostgreSQL));
        }
        final String typeName = this.typeName;
        if (context.parser().isKeyWords(typeName) || !_DialectUtils.isSimpleIdentifier(typeName)) {
            throw new CriteriaException(String.format("%s isn't postgre type name", typeName));
        }

        context.appendLiteral(TextType.INSTANCE, this.literal, EscapeMode.DEFAULT_NO_TYPE);

        sqlBuilder.append(_Constant.DOUBLE_COLON)
                .append(typeName);
    }


    @Override
    public String toString() {
        return _StringUtils.builder()
                .append(_Constant.QUOTE)
                .append(this.literal)
                .append(_Constant.QUOTE)
                .append(_Constant.DOUBLE_COLON)
                .append(this.typeName)
                .toString();
    }


}
