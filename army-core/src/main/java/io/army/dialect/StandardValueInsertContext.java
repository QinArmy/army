package io.army.dialect;

import io.army.beans.ObjectWrapper;
import io.army.criteria.Expression;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;
import java.util.List;
import java.util.Map;

final class StandardValueInsertContext extends DomainDmlContext implements _ValueInsertContext {

    static StandardValueInsertContext create(TableMeta<?> tableMeta, Dialect dialect
            , _ValuesInsert insert, List<ObjectWrapper> domainList) {

    }

    private StandardValueInsertContext(TableMeta<?> tableMeta, Dialect dialect
            , _ValuesInsert insert, List<ObjectWrapper> domainList) {
        super(tableMeta, null, dialect);

    }

    @Override
    public Collection<FieldMeta<?, ?>> fieldMetas() {
        return null;
    }

    @Override
    public Map<FieldMeta<?, ?>, Expression<?>> commonExpMap() {
        return null;
    }

    @Override
    public List<ObjectWrapper> domainList() {
        return null;
    }


}
