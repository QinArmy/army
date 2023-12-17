package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect._SqlContext;
import io.army.mapping.IntegerType;
import io.army.mapping.JsonType;
import io.army.mapping.StringType;

abstract class FuncExpUtils {
    private FuncExpUtils() {
        throw new UnsupportedOperationException();
    }


    static Expression jsonPathExp(final Object path) {
        final Expression pathExp;
        if (path instanceof String) {
            pathExp = SQLs.literal(StringType.INSTANCE, path);
        } else if (path instanceof Expression) {
            pathExp = (Expression) path;
        } else {
            throw CriteriaUtils.mustExpressionOrType("path", String.class);
        }
        return pathExp;
    }

    static Expression jsonDocExp(final Object json) {
        final Expression jsonExp;
        if (json instanceof Expression) {
            jsonExp = (Expression) json;
        } else {
            jsonExp = SQLs.literal(JsonType.TEXT, json);
        }
        return jsonExp;
    }


    static void assertPathExp(final Object path) {
        if (!(path instanceof String || path instanceof Expression)) {
            throw CriteriaUtils.mustExpressionOrType("path", String.class);
        }
    }

    static void assertIntExp(final Object intValue) {
        if (!(intValue instanceof Integer || intValue instanceof Expression)) {
            throw CriteriaUtils.mustExpressionOrType("integer value", Integer.class);
        }
    }

    static void appendJsonDoc(final Object jsonDoc, final StringBuilder sqlBuilder, final _SqlContext context) {
        if (jsonDoc instanceof Expression) {
            ((ArmyExpression) jsonDoc).appendSql(sqlBuilder, context);
        } else {
            context.appendLiteral(JsonType.TEXT, jsonDoc);
        }
    }

    static void appendPathExp(final Object pathExp, final StringBuilder sqlBuilder, final _SqlContext context) {
        if (pathExp instanceof String) {
            context.appendLiteral(StringType.INSTANCE, pathExp);
        } else if (pathExp instanceof Expression) {
            ((ArmyExpression) pathExp).appendSql(sqlBuilder, context);
        } else {
            // no bug, never here
            throw new IllegalArgumentException();
        }
    }


    static void appendIntExp(final Object intExp, final StringBuilder sqlBuilder, final _SqlContext context) {
        if (intExp instanceof Expression) {
            ((ArmyExpression) intExp).appendSql(sqlBuilder, context);
        } else {
            context.appendLiteral(IntegerType.INSTANCE, intExp);
        }
    }


}
