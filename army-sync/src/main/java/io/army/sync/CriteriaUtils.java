package io.army.sync;

import io.army.criteria.Insert;
import io.army.criteria.NullHandleMode;
import io.army.criteria.Visible;
import io.army.criteria.impl.SQLs;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.GenericCriteria;

import java.util.List;

public abstract class CriteriaUtils extends GenericCriteria {


    protected CriteriaUtils() {
    }


    public static <T extends IDomain> long save(SyncSession session, T domain) {
        return save(session, domain, NullHandleMode.INSERT_DEFAULT, Visible.ONLY_VISIBLE);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IDomain> long save(SyncSession session, T domain, NullHandleMode mode, Visible visible) {
        final TableMeta<T> table;
        table = (TableMeta<T>) session.table(domain.getClass());
        final Insert stmt;
        stmt = SQLs.valueInsert(table)
                .nullHandle(mode)
                .insertInto(table)
                .value(domain)
                .asInsert();
        return session.insert(stmt, visible);
    }

    public static <T extends IDomain> long saveBatch(SyncSession session, List<T> domainList) {
        return saveBatch(session, domainList, Visible.ONLY_VISIBLE);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IDomain> long saveBatch(SyncSession session, List<T> domainList, Visible visible) {
        final TableMeta<T> table;
        table = (TableMeta<T>) session.table(domainList.get(0).getClass());
        final Insert stmt;
        stmt = SQLs.valueInsert(table)
                .insertInto(table)
                .values(domainList)
                .asInsert();
        return session.insert(stmt, visible);
    }


}
