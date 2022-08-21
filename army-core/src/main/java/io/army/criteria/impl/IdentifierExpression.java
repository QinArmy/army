package io.army.criteria.impl;

import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.StringType;
import io.army.meta.TypeMeta;

import java.util.Objects;

final class IdentifierExpression extends OperationExpression {

    static IdentifierExpression single(String identifier) {
        Objects.requireNonNull(identifier);
        return new IdentifierExpression(identifier);
    }

    private final String identifier;

    private IdentifierExpression(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public TypeMeta typeMeta() {
        return StringType.INSTANCE;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        context.parser()
                .identifier(this.identifier, context.sqlBuilder().append(_Constant.SPACE));
    }


}
