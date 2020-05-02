package io.army.beans;

import io.army.meta.TableMeta;

public interface DomainReadonlyWrapper extends ReadonlyWrapper {

    TableMeta<?> tableMeta();

}
