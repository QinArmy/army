package io.army.beans;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import org.springframework.beans.BeanWrapper;

final class DomainReadonlyWrapperImpl extends ReadonlyWrapperImpl implements DomainReadonlyWrapper {

    private final TableMeta<?> tableMeta;

    DomainReadonlyWrapperImpl(BeanWrapper beanWrapper, TableMeta<?> tableMeta) {
        super(beanWrapper);
        this.tableMeta = tableMeta;
        Assert.isTrue(beanWrapper.getWrappedClass() == tableMeta.javaType()
                , () -> String.format("class[%s] and TableMeta[%s] not match."
                        , beanWrapper.getWrappedClass().getName(), tableMeta));

    }

    DomainReadonlyWrapperImpl(IDomain domain, TableMeta<?> tableMeta) {
        super(domain);
        Assert.isTrue(domain.getClass() == tableMeta.javaType()
                , () -> String.format("domain[%s] and TableMeta[%s] not match.", domain.getClass().getName(), tableMeta));
        this.tableMeta = tableMeta;
    }


    @Override
    public final TableMeta<?> tableMeta() {
        return this.tableMeta;
    }
}
