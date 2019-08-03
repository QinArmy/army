package io.army.meta;

import io.army.criteria.dialect.Dialect;
import io.army.util.Precision;

import java.util.List;

public interface SQLType {

    String typeName();

    String typeName(int precision);

    String typeName(int precision, int scale);

    Precision defaultPrecision();

    /**
     * @return dialect list
     */
    List<Dialect> dialectList();

}
