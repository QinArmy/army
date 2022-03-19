package io.army.example.dao.sync.dao;

import io.army.criteria.NullHandleMode;
import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.example.domain.Domain;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.sync.CurrentSessionContext;
import io.army.sync.SessionFactory;
import io.army.sync.SyncSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("syncBaseDao")
public class SyncBaseDao implements BaseDao {

    protected CurrentSessionContext sessionContext;


    @Autowired
    public void setSessionFactory(@Qualifier("exampleSyncSessionFactory") SessionFactory sessionFactory) {
        this.sessionContext = sessionFactory.currentSessionContext();
    }


    @Override
    public <T extends Domain> void save(final T domain) {
        this.sessionContext.currentSession()
                .save(domain, NullHandleMode.INSERT_DEFAULT);
    }

    @Override
    public <T extends Domain> void batchSave(List<T> domainList) {
        this.sessionContext.currentSession()
                .batchSave(domainList, NullHandleMode.INSERT_DEFAULT);
    }

    @Override
    public <T extends Domain> T get(Class<T> domainClass, Object id) {
        final SyncSession session;
        session = this.sessionContext.currentSession();
        return session.get(session.tableMeta(domainClass), id);
    }

    @Override
    public <T extends Domain> T getByUnique(Class<T> domainClass, String fieldName, Object fieldValue) {
        final SyncSession session;
        session = this.sessionContext.currentSession();
        final TableMeta<T> table;
        table = session.tableMeta(domainClass);
        return session.getByUnique(table, table.getUniqueField(fieldName), fieldValue);
    }

    @Override
    public <T extends Domain> T findById(Class<T> domainClass, Object id) {
        final SyncSession session;
        session = this.sessionContext.currentSession();
        final TableMeta<T> table;
        table = session.tableMeta(domainClass);

        final Select stmt;
        if (table instanceof ChildTableMeta) {
            final ParentTableMeta<?> parent = ((ChildTableMeta<T>) table).parentMeta();
            stmt = SQLs.query()
                    .select(SQLs.childGroup((ChildTableMeta<? extends Domain>) table, "c", "p"))
                    .from(table, "c")
                    .join(parent, "p").on(table.id().equal(parent.id()))
                    .where(table.id().equalLiteral(id))
                    .asQuery();
        } else {
            stmt = SQLs.query()
                    .select(SQLs.group(table, "t"))
                    .from(table, "t")
                    .where(table.id().equalLiteral(id))
                    .asQuery();
        }
        return session.selectOne(stmt, domainClass);
    }


    @Override
    public void flush() {
        this.sessionContext.currentSession().flush();
    }


}
