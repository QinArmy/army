package io.army.dialect.postgre;

import io.army.dialect.Dialect;
import io.army.dialect._AbstractDialect;
import io.army.dialect._DialectEnvironment;
import io.army.meta.ParamMeta;
import io.army.stmt.Stmt;

import java.util.Set;

class Postgre11DmlDialect extends _AbstractDialect {

    Postgre11DmlDialect(_DialectEnvironment environment) {
        super(environment);
    }

    @Override
    public boolean supportOnlyDefault() {
        return false;
    }

    @Override
    public void clearForDDL() {

    }

    @Override
    protected final boolean supportTableOnly() {
        //Postgre support 'ONLY' key word before table name.
        return true;
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
    public String showSQL(Stmt stmt) {
        return null;
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
    public Dialect mode() {
        return null;
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
    protected Set<String> createKeyWordSet() {
        return null;
    }

    @Override
    protected String quoteIdentifier(String identifier) {
        return null;
    }
}
