package io.army.dialect;

import io.army.criteria.NamedLiteral;
import io.army.criteria.SQLParam;
import io.army.criteria.SubQuery;
import io.army.criteria.Visible;

import javax.annotation.Nullable;

import io.army.meta.TypeMeta;

/**
 * <p>
 * This interface representing sql context,that is used by {@link  DialectParser} and the implementation of criteria api,
 * for example {@link  io.army.criteria.impl.inner._Expression}.
 * * @since 1.0
 */
public interface _SqlContext extends SqlContextSpec {


    Database database();

    Dialect dialect();


    StringBuilder appendFuncName(boolean buildIn, String name);



    void appendSubQuery(SubQuery query);

    DialectParser parser();

    StringBuilder sqlBuilder();

    /**
     * <p>
     * This method is designed for parameter expression.
     *     * <p> steps:
     *     <ol>
     *         <li>append one space</li>
     *         <li>append '?' to {@link #sqlBuilder()}</li>
     *         <li>append sqlParam to param list</li>
     *     </ol>
     *     */
    void appendParam(SQLParam sqlParam);

    void appendLiteral(TypeMeta typeMeta, @Nullable Object value);

    void appendLiteral(NamedLiteral namedLiteral);

    /**
     * @see DialectParser#identifier(String, StringBuilder)
     */
    StringBuilder identifier(String identifier, StringBuilder builder);

    /**
     * @see DialectParser#identifier(String)
     */
    String identifier(String identifier);

    Visible visible();


}
