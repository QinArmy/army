package io.army.dialect;

import io.army.meta.FieldMeta;

/**
 * this class is a  {@link Dialect} implementation and represent Oracle 12g  Dialect
 * created  on 2018/10/21.
 */
public class Oracle12Dialect extends AbstractDialect {

    public static final Oracle12Dialect INSTANCE = new Oracle12Dialect();

    @Override
    public String fieldDefinition(FieldMeta<?, ?> field) {
        return null;
    }
}
