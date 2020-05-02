package io.army.beans;

import io.army.criteria.impl.TableMetaFactory;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

final class DomainWrapperImpl extends BeanWrapperImpl implements DomainWrapper {

    private final TableMeta<?> tableMeta;

    DomainWrapperImpl(IDomain domain) {
        super(domain);
        this.tableMeta = TableMetaFactory.getTableMeta(domain.getClass());
        ;
    }


    @Override
    public final DomainReadonlyWrapper getReadonlyWrapper() {
        if (this.readonlyWrapper == null || !(this.readonlyWrapper instanceof DomainReadonlyWrapper)) {
            this.readonlyWrapper = new DomainReadonlyWrapperImpl(this.actualWrapper, this.tableMeta);
        }
        return (DomainReadonlyWrapper) readonlyWrapper;
    }

    @Override
    public final TableMeta<?> tableMeta() {
        return this.tableMeta;
    }
}
