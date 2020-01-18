package io.army.asm;

import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;

public interface TableMetaLoader {

    /**
     *
     * @param basePackages domain's packages
     * @return a unmodifiable Map
     */
    Map<Class<?>, TableMeta<?>> scanTableMeta(List<String> basePackages);

    static TableMetaLoader create() {
        return new TableMetaLoaderIml();
    }
}
