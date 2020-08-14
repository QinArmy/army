package com.example.fortune.dao.sync.mysql57;

import com.example.fortune.dao.sync.FortuneSyncBaseDao;
import com.example.fortune.dao.sync.user.UserDao;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository("mySQLUserDao")
@Profile({"sync", "MySQL57"})
public class MySQL57UserDao extends FortuneSyncBaseDao implements UserDao {


}
