package io.army.meta;

/**
 * represent a schema.
 * <ul>
 *     <li> MySQL : database</li>
 *     <li> Oracle : schema</li>
 * </ul>
 *
 * @see TableMeta
 */
public interface SchemaMeta extends Meta {


    String catalog();


    String schema();

    boolean defaultSchema();

    @Override
    boolean equals(Object o);

    @Override
    String toString();
}
