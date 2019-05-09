package org.qinarmy.army.meta;

import org.qinarmy.army.criteria.dialect.Dialect;
import org.qinarmy.army.domain.IDomain;

import java.nio.charset.Charset;
import java.util.List;

/**
 * created  on 2018/10/8.
 */
public interface TableMeta<T extends IDomain> {

    Class<T> javaType();

    String tableName();

    boolean immutable();

    int fieldCount();

    String comment();

    Field<T, ?> primaryKey();


    MappingMode mappingMode();

    List<String> indexPropList();

    List<String> uniquePropList();

    List<Field<T, ?>> fieldList();

    String createSql(Dialect dialect);

    Charset charset();

}
