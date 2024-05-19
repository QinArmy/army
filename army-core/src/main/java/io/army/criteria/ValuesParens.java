package io.army.criteria;

import io.army.criteria.standard.SQLs;

import java.util.function.Consumer;

/**
 * <p>This interface representing dynamic row clause in  VALUES statement.
 * <p>This interface is similar to {@link ValuesRows}, except method name
 *
 * @see ValuesRows
 */
public interface ValuesParens {

    ValuesParens parens(Consumer<Values._ValueStaticColumnSpaceClause> consumer);

    /**
     * <p>Create new row
     */
    ValuesParens parens(SQLs.SymbolSpace space, Consumer<Values._ValuesDynamicColumnClause> consumer);

}
