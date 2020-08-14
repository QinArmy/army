package com.example.fortune.dao.sync;

import com.example.dao.AbstractBaseDay;
import io.army.sync.GenericSyncProxySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;


@Repository("fortuneSyncBaseDao")

public class FortuneSyncBaseDao extends AbstractBaseDay implements FortuneSyncDao {


    @Autowired
    public void setSession(@Qualifier("fortuneProxySession") GenericSyncProxySession session) {
        this.session = session;
    }

}
