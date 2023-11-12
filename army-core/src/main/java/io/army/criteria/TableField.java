package io.army.criteria;

import io.army.annotation.GeneratorType;
import io.army.annotation.UpdateMode;
import io.army.mapping.MappingType;
import io.army.meta.DatabaseObject;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * <p>
 * This interface representing column of database table. This interface is base interface of below interfaces:
 * <ul>
 *     <li>{@link FieldMeta}</li>
 *      <li>{@link QualifiedField}</li>
 * </ul>
 * </p>
 *
 * @see FieldMeta
 * @see QualifiedField
 */
@SuppressWarnings("unused")
public interface TableField extends SqlField, FieldSelection, TypeMeta, DatabaseObject {

    TableMeta<?> tableMeta();

    /**
     * @return domain mapping property java type.
     */
    Class<?> javaType();

    MappingType mappingType();

    /**
     * @return mapping  field name,see {@link Field#getName()}.
     */
    String fieldName();

    /**
     * <p>
     * Equivalence : {@link  FieldMeta#objectName()}
     * </p>
     *
     * @return column name(lower case).
     */
    String columnName();


    boolean codec();

    boolean nullable();

    UpdateMode updateMode();

    boolean insertable();

    @Nullable
    GeneratorType generatorType();



    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();


}
