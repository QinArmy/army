package io.army.dialect;

import io.army.meta.DatabaseObject;

/**
 * package interface
 */
interface ArmyDialect extends DialectParser {


    boolean singleDeleteHasTableAlias();

    boolean hasRowKeywords();

    default boolean supportRowLeftItem() {
        return false;
    }

    default boolean supportQueryUpdate() {
        return false;
    }


    boolean supportMultiUpdate();


    boolean supportZone();

    boolean supportOnlyDefault();


    boolean tableAliasAfterAs();


    boolean supportInsertReturning();

    boolean isMockEnv();

    _FieldValueGenerator getFieldValueGenerator();


    String defaultFuncName();

    String safeObjectName(DatabaseObject object);

    StringBuilder safeObjectName(DatabaseObject object, StringBuilder builder);


}
