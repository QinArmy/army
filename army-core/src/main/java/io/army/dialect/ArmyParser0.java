package io.army.dialect;

import io.army.mapping.MappingEnv;
import io.army.meta.DatabaseObject;
import io.army.meta.TypeMeta;

/**
 * package interface
 */
@Deprecated
interface ArmyParser0 extends DialectParser {


    @Deprecated
    default boolean singleDeleteHasTableAlias() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default boolean hasRowKeywords() {
        throw new UnsupportedOperationException();
    }


    @Deprecated
    default boolean supportMultiUpdate() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
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
    StringBuilder literal(TypeMeta paramMeta, Object nonNull, StringBuilder sqlBuilder);


    MappingEnv mappingEnv();

}
