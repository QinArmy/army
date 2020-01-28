package io.army.asm;

import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface TableMetaLoader {

    /**
     * @param basePackages domain's packages
     * @return a unmodifiable Map
     */
    Map<Class<?>, TableMeta<?>> scanTableMeta(SchemaMeta schemaMeta, List<String> basePackages)
            throws TableMetaLoadException;

    static TableMetaLoader build() {
        return new TableMetaLoaderIml();
    }
}
