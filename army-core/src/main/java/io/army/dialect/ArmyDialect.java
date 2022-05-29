package io.army.dialect;

/**
 * package interface
 */
interface ArmyDialect extends _Dialect {


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



}
