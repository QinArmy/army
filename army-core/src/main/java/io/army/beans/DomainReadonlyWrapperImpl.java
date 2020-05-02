package io.army.beans;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import org.springframework.beans.BeanWrapper;

final class DomainReadonlyWrapperImpl extends ReadonlyWrapperImpl implements DomainReadonlyWrapper {

    private final TableMeta<?> tableMeta;

    DomainReadonlyWrapperImpl(BeanWrapper beanWrapper, TableMeta<?> tableMeta) {
        super(beanWrapper);
        this.tableMeta = tableMeta;
    }

    DomainReadonlyWrapperImpl(IDomain domain, TableMeta<?> tableMeta) {
        super(domain);
        this.tableMeta = tableMeta;
    }


    @Override
    public final TableMeta<?> tableMeta() {
        return this.tableMeta;
    }
}
