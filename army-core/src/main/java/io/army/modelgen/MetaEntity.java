package io.army.modelgen;

/**
 * this interface create parts of meta source code.
 * created  on 2018/11/18.
 * @see io.army.annotation.Table
 * @see io.army.meta.TableMeta
 * @see MetaAttribute
 */
interface MetaEntity {

    String getQualifiedName();

    String getImportBlock();

    String getClassDefinition();

    String getBody();

}
