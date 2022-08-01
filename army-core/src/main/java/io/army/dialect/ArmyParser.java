package io.army.dialect;

import io.army.criteria.SubQuery;
import io.army.mapping.MappingEnv;
import io.army.meta.DatabaseObject;
import io.army.meta.ParamMeta;

/**
 * package interface
 */
interface ArmyParser extends DialectParser {


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

    FieldValueGenerator getGenerator();


    String defaultFuncName();

    String safeObjectName(DatabaseObject object);

    StringBuilder safeObjectName(DatabaseObject object, StringBuilder builder);

    /**
     * <p>
     * Append  literal
     * </p>
     */
    StringBuilder literal(ParamMeta paramMeta, Object nonNull, StringBuilder sqlBuilder);


    MappingEnv mappingEnv();

    void subQueryOfQueryInsert(_QueryInsertContext outerContext, SubQuery subQuery);


}
