package io.army.dialect;

import io.army.beans.ObjectWrapper;
import io.army.criteria.Expression;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.session.FactoryMode;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

final class StandardValueInsertContext extends DomainDmlContext implements _ValueInsertContext {

    static StandardValueInsertContext create(_ValuesInsert insert, Dialect dialect) {

        final TableMeta<?> tableMeta = insert.tableMeta();

        final List<ObjectWrapper> domainList = insert.domainList();
        final StandardValueInsertContext context;
        if (dialect.sessionFactory().factoryMode() == FactoryMode.NO_SHARDING || domainList.size() == 1) {
            context = new StandardValueInsertContext(tableMeta, )
        } else {

        }
    }

    private final Collection<FieldMeta<?, ?>> fieldMetas;

    private final Map<FieldMeta<?, ?>, Expression<?>> commonExpMap;

    private final List<ObjectWrapper> domainList;

    /**
     * <p>
     * create instance for {@link FactoryMode#NO_SHARDING}.
     * </p>
     */
    private StandardValueInsertContext(_ValuesInsert insert, Dialect dialect) {
        super(insert.tableMeta(), null, dialect);
        this.fieldMetas = insert.fieldSet();
        this.commonExpMap = insert.commonExpMap();
        this.domainList = insert.domainList();
    }


    private StandardValueInsertContext(TableMeta<?> tableMeta, Dialect dialect
            , _ValuesInsert insert, Collection<FieldMeta<?, ?>> fieldMetas
            , List<ObjectWrapper> domainList) {
        super(tableMeta, null, dialect);
        this.commonExpMap = insert.commonExpMap();
        this.fieldMetas = fieldMetas;
        switch (domainList.size()) {
            case 0:
                throw new IllegalArgumentException("domainList is empty");
            case 1:
                this.domainList = Collections.singletonList(domainList.get(0));
                break;
            default:
                this.domainList = Collections.unmodifiableList(domainList);
        }

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
