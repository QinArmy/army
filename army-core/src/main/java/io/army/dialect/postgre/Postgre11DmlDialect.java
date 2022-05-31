package io.army.dialect.postgre;

import io.army.dialect.*;
import io.army.meta.DatabaseObject;
import io.army.meta.ParamMeta;
import io.army.meta.ServerMeta;
import io.army.tx.Isolation;
import io.army.util._Exceptions;

import java.util.List;
import java.util.Set;

class Postgre11DmlDialect extends _AbstractDialect {

    Postgre11DmlDialect(_DialectEnvironment environment, Dialect dialect) {
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
    public StringBuilder literal(ParamMeta paramMeta, Object nonNull, StringBuilder sqlBuilder) {
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
    protected final void standardLimitClause(final long offset, final long rowCount, final _SqlContext context) {
        if (offset >= 0L && rowCount >= 0L) {
            context.sqlBuilder().append(_Constant.SPACE_LIMIT_SPACE)
                    .append(rowCount)
                    .append(_Constant.SPACE_OFFSET_SPACE)
                    .append(offset);
        } else if (rowCount >= 0L) {
            context.sqlBuilder().append(_Constant.SPACE_LIMIT_SPACE)
                    .append(rowCount);
        } else if (offset >= 0L) {
            throw _Exceptions.standardLimitClauseError(offset, rowCount);
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
