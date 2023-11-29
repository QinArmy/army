package io.army.example.pill.dao.sync.dao;

import io.army.example.bank.dao.sync.BankSyncBaseDao;
import io.army.sync.SyncSessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("pillSyncBaseDao")
public class PillSyncBaseDao extends BankSyncBaseDao {


    @Autowired
    public void setSessionContext(@Qualifier("pillSyncSessionContext") SyncSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

}
