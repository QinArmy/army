package io.army.criteria.postgre;

import io.army.mapping.MappingType;

/**
 * <p>
 * This interface not start with underscore, so this interface can present in application developer code.
 * * @since 1.0
 */
public interface FuncColumnDefCommaClause {

    FuncColumnDefCommaClause comma(String name, MappingType type);


}
