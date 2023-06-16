package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.LiteralExpression;
import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.dialect._DialectUtils;
import io.army.dialect._SqlContext;
import io.army.mapping.NoCastTextType;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

/**
 * <p>
 * This class representing PostgreSQL-style typecast expression
 * </p>
 *
 * @since 1.0
 */
final class PostgreDoubleColonCastExpression extends OperationExpression.OperationSimpleExpression
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
    public void appendSql(final _SqlContext context) {
        if (context.database() != Database.PostgreSQL) {
            throw new CriteriaException(String.format("dialect database isn't %s", Database.PostgreSQL));
        }
        final String typeName = this.typeName;
        if (context.parser().isKeyWords(typeName) || !_DialectUtils.isSimpleIdentifier(typeName)) {
            throw new CriteriaException(String.format("%s isn't postgre type name", typeName));
        }

        context.appendLiteral(NoCastTextType.INSTANCE, this.literal);


        context.sqlBuilder()
                .append(_Constant.DOUBLE_COLON)
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
