package io.army.schema;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

/**
 * @see _SchemaResult
 */
public interface _TableResult {

    TableMeta<?> table();

    boolean comment();

    List<FieldMeta<?, ?>> newFieldList();

    List<_FieldResult> changeFieldList();

    List<String> newIndexList();

    List<String> changeIndexList();

    List<String> droopIndexList();


}
