package io.army.dialect;

import io.army.criteria.TableAliasException;
import io.army.criteria.impl.inner.InnerDomainUpdate;
import io.army.meta.FieldMeta;

public interface DomainUpdateContext extends UpdateContext {

    @Override
    InnerDomainUpdate innerUpdate();


    @Override
    void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException;

    @Override
    void appendField(FieldMeta<?, ?> fieldMeta);
}
