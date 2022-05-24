package io.army.dialect.postgre;

import io.army.dialect.Dialect;
import io.army.dialect._AbstractDialect;
import io.army.dialect._Constant;
import io.army.dialect._DialectEnvironment;
import io.army.meta.ParamMeta;
import io.army.meta.ServerMeta;
import io.army.tx.Isolation;

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
    protected final char identifierQuote() {
        return _Constant.DOUBLE_QUOTE;
    }

    @Override
    protected final boolean identifierCaseSensitivity() {
        //Postgre identifier not case sensitivity
        return false;
    }

    @Override
    protected final PostgreDdl createDdlDialect() {
        return PostgreDdl.create(this);
    }

    @Override
    public String literal(ParamMeta paramMeta, Object nonNull) {
        return null;
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
    public boolean multiTableUpdateChild() {
        return false;
    }

    @Override
    protected Set<String> createKeyWordSet(ServerMeta meta) {
        return null;
    }


}
