package io.army.modelgen;

/**
 * debugSQL mapping props part of meta source code
 * <p>
 * see {@code io.army.meta.FieldMeta}
 * </p>
 *
 * @see io.army.annotation.Column
 * @see MetaEntity
 * @since 1.0
 */
 interface MetaAttribute {


    String getDefinition();

    String getName();

    String getNameDefinition();


}
