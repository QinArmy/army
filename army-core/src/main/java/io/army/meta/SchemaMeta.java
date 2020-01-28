package io.army.meta;

import io.army.domain.IDomain;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * represent a schema.
 * <ul>
 *     <li> MySQL : database</li>
 *     <li> Oracle : schema</li>
 * </ul>
 *
 * @see TableMeta
 */
public interface SchemaMeta {

    @NonNull
    String catalog();

    @NonNull
    String schema();

    boolean defaultSchema();

    @Override
    boolean equals(Object o);

    @Override
    String toString();
}
