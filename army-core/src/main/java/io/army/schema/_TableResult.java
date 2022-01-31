package io.army.schema;

import io.army.meta.FieldMeta;

import java.util.List;

/**
 * @see _SchemaResult
 */
public interface _TableResult {

    boolean comment();

    List<FieldMeta<?, ?>> newFieldList();

    List<_FieldResult> changeFieldList();

    List<String> newIndexList();

    List<String> changeIndexList();

    List<String> droopIndexList();


}
