package io.army.example.bank.dao.sync;

import io.army.criteria.Select;
import io.army.example.common.ArmySyncBaseDao;
import io.army.example.common.Pair;
import io.army.sync.SessionFactory;
import io.army.sync.SyncSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("bankSyncBaseDao")
public class BankSyncBaseDao extends ArmySyncBaseDao {

    @Autowired
    public void setSessionFactory(@Qualifier("bankSyncSessionFactory") SessionFactory sessionFactory) {
        this.sessionContext = sessionFactory.currentSessionContext();
    }

    @SuppressWarnings("unchecked")
    protected final <F, S> Pair<F, S> selectAsPair(SyncSession session, Select stmt) {
        return (Pair<F, S>) session.selectOne(stmt, Pair.class);
    }


}
