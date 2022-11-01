package io.army.dialect.postgre;

import io.army.criteria.impl.inner._Expression;
import io.army.dialect.DialectEnv;
import io.army.dialect._AbstractDialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.DatabaseObject;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.tx.Isolation;

import java.util.List;
import java.util.Set;

abstract class PostgreParser extends _AbstractDialectParser {

    PostgreParser(DialectEnv environment, PostgreDialect dialect) {
        super(environment, dialect);
    }


    @Override
    public List<String> startTransaction(Isolation isolation, boolean readonly) {
        return null;
    }

    @Override
    public boolean supportOnlyDefault() {
        return false;
    }


    @Override
    protected final boolean supportTableOnly() {
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
    public boolean supportRowLeftItem() {
        return super.supportRowLeftItem();
    }

    @Override
    public boolean supportQueryUpdate() {
        return super.supportQueryUpdate();
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
    public boolean setClauseSupportRow() {
        return super.setClauseSupportRow();
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
    public boolean supportInsertReturning() {
        return false;
    }

    @Override
    public boolean supportZone() {
        return false;
    }

    @Override
    public boolean tableAliasAfterAs() {
        return false;
    }

    @Override
    public boolean singleDeleteHasTableAlias() {
        return false;
    }

    @Override
    public boolean hasRowKeywords() {
        return false;
    }


    @Override
    public boolean supportSavePoint() {
        return false;
    }

    @Override
    public boolean setClauseTableAlias() {
        return false;
    }


    @Override
    public String defaultFuncName() {
        return null;
    }

    @Override
    public boolean supportMultiUpdate() {
        return false;
    }

    @Override
    protected Set<String> createKeyWordSet(ServerMeta meta) {
        return null;
    }


}
