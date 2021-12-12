package io.army.dialect;

import io.army.beans.ObjectWrapper;
import io.army.criteria.Expression;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.session.FactoryMode;
import io.army.session.GenericRmSessionFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

final class StandardValueInsertContext extends DomainDmlContext implements _ValueInsertContext {

    static StandardValueInsertContext create(_ValuesInsert insert, Dialect dialect, Visible visible) {

        if (dialect.sessionFactory().factoryMode() != FactoryMode.NO_SHARDING) {
            final GenericRmSessionFactory factory = dialect.sessionFactory();
            String m = String.format("%s %s[%s] isn't %s", factory, FactoryMode.class.getName()
                    , factory.factoryMode(), FactoryMode.NO_SHARDING);
            throw new IllegalArgumentException(m);
        }
        return new StandardValueInsertContext(insert, dialect);
    }

    static StandardValueInsertContext sharding(_ValuesInsert insert, final byte database
            , List<ObjectWrapper> domainList, Dialect dialect, Visible visible) {
        return new StandardValueInsertContext(insert, database, domainList, dialect);
    }

    private final List<FieldMeta<?, ?>> fieldMetas;

    private final Map<FieldMeta<?, ?>, Expression<?>> commonExpMap;

    private final List<ObjectWrapper> domainList;

    private final _ValueInsertContext parentContext;

    /**
     * <p>
     * create parent context for {@link FactoryMode#NO_SHARDING}.
     * </p>
     */
    private StandardValueInsertContext(Dialect dialect, _ValuesInsert insert) {
        super(insert.tableMeta(), null, (byte) 0, dialect);
        this.fieldMetas = insert.parentFieldSet();
        this.commonExpMap = insert.commonExpMap();
        this.domainList = insert.domainList();

        this.parentContext = null;
    }

    /**
     * <p>
     * create table context for {@link FactoryMode#NO_SHARDING}.
     * </p>
     */
    private StandardValueInsertContext(_ValuesInsert insert, Dialect dialect) {
        super(insert.tableMeta(), null, (byte) 0, dialect);
        this.fieldMetas = insert.fieldList();
        this.commonExpMap = insert.commonExpMap();
        this.domainList = insert.domainList();

        if (insert.tableMeta() instanceof ChildTableMeta) {
            this.parentContext = new StandardValueInsertContext(dialect, insert);
        } else {
            this.parentContext = null;
        }

    }

    /**
     * <p>
     * create parent context for {@link FactoryMode#SHARDING} or {@link FactoryMode#TABLE_SHARDING}.
     * </p>
     */
    private StandardValueInsertContext(_ValuesInsert insert, final byte database
            , List<ObjectWrapper> domainList, Dialect dialect) {
        super(insert.tableMeta(), null, database, dialect);
        this.commonExpMap = insert.commonExpMap();
        this.fieldMetas = insert.fieldList();
        switch (domainList.size()) {
            case 0:
                throw new IllegalArgumentException("domainList is empty");
            case 1:
                this.domainList = Collections.singletonList(domainList.get(0));
                break;
            default:
                this.domainList = Collections.unmodifiableList(domainList);
        }
        if (insert.tableMeta() instanceof ChildTableMeta) {
            this.parentContext = new StandardValueInsertContext(dialect, insert, database, domainList);
        } else {
            this.parentContext = null;
        }

    }

    /**
     * <p>
     * create parent context for {@link FactoryMode#SHARDING} or {@link FactoryMode#TABLE_SHARDING}.
     * </p>
     *
     * @see #StandardValueInsertContext(_ValuesInsert, byte, List, Dialect)
     */
    private StandardValueInsertContext(Dialect dialect, _ValuesInsert insert, final byte database
            , List<ObjectWrapper> domainList) {
        super(((ChildTableMeta<?>) insert.tableMeta()).parentMeta(), null, database, dialect);
        this.commonExpMap = insert.commonExpMap();
        this.fieldMetas = insert.fieldList();
        switch (domainList.size()) {
            case 0:
                throw new IllegalArgumentException("domainList is empty");
            case 1:
                this.domainList = Collections.singletonList(domainList.get(0));
                break;
            default:
                this.domainList = Collections.unmodifiableList(domainList);
        }
        this.parentContext = null;
    }

    @Override
    public List<FieldMeta<?, ?>> fields() {
        return this.fieldMetas;
    }

    @Override
    public Map<FieldMeta<?, ?>, Expression<?>> commonExpMap() {
        return this.commonExpMap;
    }

    @Override
    public List<ObjectWrapper> domainList() {
        return this.domainList;
    }

    @Override
    public _ValueInsertContext parentContext() {
        return this.parentContext;
    }


}
