package io.army.dialect;

import io.army.meta.SingleTableMeta;


/**
 * package interface
 */
interface ArmyDialect extends _Dialect {

    boolean isMockEnv();

    FieldValueGenerator getFieldValueGenerator();

    void appendArmyManageFieldsToSetClause(final SingleTableMeta<?> table, final String safeTableAlias
            , final _SqlContext context);


}
