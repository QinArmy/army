package com.example.fortune.dao.sync.mysql57.user;

import com.example.fortune.dao.sync.FortuneSyncBaseDao;
import com.example.fortune.dao.sync.delegate.user.UserDao;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository("mySQL57UserDao")
@Profile({"sync", "MySQL57"})
public class MySQL57UserDao extends FortuneSyncBaseDao implements UserDao {


}
