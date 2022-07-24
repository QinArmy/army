package io.army.example.common;

import io.army.criteria.NullHandleMode;
import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.sync.SessionContext;
import io.army.sync.SyncSession;

import java.util.List;
import java.util.Map;

public abstract class ArmySyncBaseDao implements SyncBaseDao {

    protected SessionContext sessionContext;


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
        return session.queryOne(createFindByIdStmt(session, domainClass, id), domainClass);
    }

    @Override
    public Map<String, Object> findByIdAsMap(Class<?> domainClass, Object id) {
        final SyncSession session;
        session = this.sessionContext.currentSession();
        final Select stmt;
        stmt = createFindByIdStmt(session, domainClass, id);
        return session.queryOneAsMap(stmt);
    }

    @Override
    public void flush() {
        this.sessionContext.currentSession().flush();
    }


    protected <T> Select createFindByIdStmt(SyncSession session, Class<T> domainClass, Object id) {
        final TableMeta<T> table;
        table = session.tableMeta(domainClass);

        final Select stmt;
        if (table instanceof ChildTableMeta) {
            final ChildTableMeta<T> child = (ChildTableMeta<T>) table;
            final ParentTableMeta<?> parent = child.parentMeta();
            stmt = SQLs.query()
                    .select(SQLs.childGroup(child, "c", "p"))
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
        return stmt;
    }


}
