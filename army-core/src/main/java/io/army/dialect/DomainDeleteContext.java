package io.army.dialect;

import io.army.criteria.TableAliasException;
import io.army.criteria.impl.inner.InnerDomainDelete;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

public interface DomainDeleteContext extends DeleteContext {

    @Override
    InnerDomainDelete innerDelete();

    TableMeta<?> tableMeta();

    String tableAlias();

    @Override
    void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException;

    @Override
    void appendField(FieldMeta<?, ?> fieldMeta);
}
