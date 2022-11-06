package io.army.dialect.postgre;

import io.army.criteria.impl.inner._Expression;
import io.army.dialect.*;
import io.army.lang.Nullable;
import io.army.meta.DatabaseObject;
import io.army.meta.TypeMeta;
import io.army.tx.Isolation;

import java.util.List;
import java.util.Set;

abstract class PostgreParser extends _ArmyDialectParser {

    PostgreParser(DialectEnv environment, PostgreDialect dialect) {
        super(environment, dialect);
    }


    @Override
    public List<String> startTransaction(Isolation isolation, boolean readonly) {
        return null;
    }

    @Override
    public boolean isSupportOnlyDefault() {
        return false;
    }


    @Override
    protected final boolean isSupportTableOnly() {
        //Postgre support 'ONLY' key word before table name.
        return true;
    }

    @Override
    public StringBuilder literal(TypeMeta paramMeta, Object nonNull, StringBuilder sqlBuilder) {
        return null;
    }

    @Override
    protected final char identifierQuote() {
        return _Constant.DOUBLE_QUOTE;
    }

    @Override
    protected final boolean isIdentifierCaseSensitivity() {
        //Postgre identifier not case sensitivity
        return false;
    }

    @Override
    protected final PostgreDdl createDdlDialect() {
        return PostgreDdl.create(this);
    }




    @Override
    public String safeObjectName(DatabaseObject object) {
        return null;
    }

    @Override
    public StringBuilder safeObjectName(DatabaseObject object, StringBuilder builder) {
        return builder;
    }



    @Override
    protected final void standardLimitClause(final @Nullable _Expression offset, final @Nullable _Expression rowCount
            , _SqlContext context) {
        if (offset != null && rowCount != null) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder().append(_Constant.SPACE_LIMIT_SPACE);
            rowCount.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_OFFSET_SPACE);
            offset.appendSql(context);
        } else if (rowCount != null) {
            context.sqlBuilder().append(_Constant.SPACE_LIMIT_SPACE);
            rowCount.appendSql(context);
        }
    }

    @Override
    protected Set<String> createKeyWordSet() {
        return null;
    }

    @Override
    protected String defaultFuncName() {
        return null;
    }

    @Override
    protected boolean isSupportZone() {
        return false;
    }

    @Override
    protected boolean isSetClauseTableAlias() {
        return false;
    }

    @Override
    protected boolean isTableAliasAfterAs() {
        return false;
    }

    @Override
    protected _ChildUpdateMode childUpdateMode() {
        return null;
    }

    @Override
    protected boolean isSupportSingleUpdateAlias() {
        return false;
    }

    @Override
    protected boolean isSupportSingleDeleteAlias() {
        return false;
    }

    @Override
    protected boolean isSupportUpdateRow() {
        return false;
    }

    @Override
    protected boolean isSupportUpdateDerivedField() {
        return false;
    }
}
