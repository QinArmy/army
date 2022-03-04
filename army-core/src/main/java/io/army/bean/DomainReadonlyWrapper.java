package io.army.bean;

import io.army.meta.TableMeta;

public interface DomainReadonlyWrapper extends ReadWrapper {

    TableMeta<?> tableMeta();

}
