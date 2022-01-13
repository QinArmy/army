package io.army.dialect.mysql;

import io.army.DialectMode;
import io.army.dialect.Database;
import io.army.dialect.DialectEnvironment;
import io.army.dialect._AbstractDialect;
import io.army.meta.ParamMeta;
import io.army.stmt.Stmt;

import java.util.Set;

abstract class MySQLDialect extends _AbstractDialect {

    MySQLDialect(DialectEnvironment environment) {
        super(environment);
    }


    @Override
    public void clearForDDL() {

    }


    @Override
    public boolean supportZone() {
        return false;
    }

    @Override
    public boolean supportOnlyDefault() {
        return false;
    }

    @Override
    public Database database() {
        return null;
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
    public String literal(ParamMeta paramMeta, Object value) {
        return null;
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
    public DialectMode mode() {
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
