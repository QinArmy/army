package io.army.beans;

import io.army.criteria.impl.TableMetaFactory;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.Assert;

final class DomainWrapperImpl extends BeanWrapperImpl implements DomainWrapper {


    private final TableMeta<?> tableMeta;

    DomainWrapperImpl(IDomain domain) {
        super(domain);
        this.tableMeta = TableMetaFactory.getTableMeta(domain.getClass());

    }

    DomainWrapperImpl(Object target, TableMeta<?> tableMeta) {
        super(target);
        Assert.isTrue(target.getClass() == tableMeta.javaType(), "target and tableMeta not match.");
        this.tableMeta = tableMeta;
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