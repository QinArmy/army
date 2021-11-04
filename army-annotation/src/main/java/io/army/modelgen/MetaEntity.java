package io.army.modelgen;

/**
 * this interface debugSQL parts of meta source code.
 *
 * @see io.army.annotation.Table
 * @see io.army.meta.TableMeta
 * @see MetaAttribute
 * @since 1.0
 */
interface MetaEntity {

    String getQualifiedName();

    String getImportBlock();

    String getClassDefinition();

    String getBody();

}
