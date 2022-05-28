package io.army.dialect;

import io.army.meta.SingleTableMeta;


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


    boolean multiTableUpdateChild();


    boolean supportZone();

    boolean supportOnlyDefault();


    boolean tableAliasAfterAs();


    boolean supportInsertReturning();

    boolean isMockEnv();

    _FieldValueGenerator getFieldValueGenerator();

    void appendArmyManageFieldsToSetClause(final SingleTableMeta<?> table, final String safeTableAlias
            , final _SqlContext context);


}
