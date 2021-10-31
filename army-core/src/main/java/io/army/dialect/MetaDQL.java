package io.army.dialect;

import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;

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


    SimpleStmt currentSchemaMeta();

    SimpleStmt tableMeta(SchemaMeta schemaMeta);

    SimpleStmt columnMeta(TableMeta<?> tableMeta);

    SimpleStmt indexMeta(TableMeta<?> tableMeta);

}
