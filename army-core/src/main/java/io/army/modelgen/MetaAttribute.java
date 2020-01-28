package io.army.modelgen;

/**
 * build mapping props part of meta source code
 * created  on 2018/11/18.
 * @see io.army.annotation.Column
 * @see io.army.meta.FieldMeta
 * @see MetaEntity
 */
 interface MetaAttribute {


    String getDefinition();

    String getName();

    String getNameDefinition();


}
