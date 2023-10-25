package io.army.example.pill.dao.sync.dao;

import io.army.example.bank.dao.sync.BankSyncBaseDao;
import io.army.sync.SyncLocalSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("pillSyncBaseDao")
public class PillSyncBaseDao extends BankSyncBaseDao {


    @Autowired
    public void setSessionFactory(@Qualifier("pillSyncSessionFactory") SyncLocalSessionFactory sessionFactory) {
        this.sessionContext = sessionFactory.currentSessionContext();
    }

}
