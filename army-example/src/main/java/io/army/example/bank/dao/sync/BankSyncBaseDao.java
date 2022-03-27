package io.army.example.bank.dao.sync;

import io.army.example.common.ArmySyncBaseDao;
import io.army.sync.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("bankSyncBaseDao")
public class BankSyncBaseDao extends ArmySyncBaseDao {

    @Autowired
    public void setSessionFactory(@Qualifier("bankSyncSessionFactory") SessionFactory sessionFactory) {
        this.sessionContext = sessionFactory.currentSessionContext();
    }


}
