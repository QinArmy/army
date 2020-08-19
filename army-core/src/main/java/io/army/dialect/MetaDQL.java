package io.army.dialect;

import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.wrapper.SimpleSQLWrapper;

/**
 * DQL of below meta:
 * <ul>
 *     <li>catalog meta data</li>
 *     <li>support save point meta data</li>
 *     <li>schema meta data</li>
 *     <li>table meta data</li>
 *     <li>column meta data</li>
 *     <li>index meta data</li>
 * </ul>
 * <p>
 *     This interface design for reactive database driver(eg:R2DBC).
 * </p>
 */
public interface MetaDQL extends SQL {


    SimpleSQLWrapper currentSchemaMeta();

    SimpleSQLWrapper tableMeta(SchemaMeta schemaMeta);

    SimpleSQLWrapper columnMeta(TableMeta<?> tableMeta);

    SimpleSQLWrapper indexMeta(TableMeta<?> tableMeta);

}
