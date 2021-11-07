package io.army.asm;

import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.meta.TableMetaLoadException;

import java.util.List;
import java.util.Map;

@Deprecated
public interface TableMetaLoader {

    /**
     * @param basePackages domain's packages
     * @return a unmodifiable Map
     */
    Map<Class<?>, TableMeta<?>> scanTableMeta(SchemaMeta schemaMeta, List<String> basePackages)
            throws TableMetaLoadException;

    static TableMetaLoader build() {
        throw new UnsupportedOperationException();
    }
}
