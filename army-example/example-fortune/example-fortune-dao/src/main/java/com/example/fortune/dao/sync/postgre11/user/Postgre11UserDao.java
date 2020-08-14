package com.example.fortune.dao.sync.postgre11.user;

import com.example.fortune.dao.sync.FortuneSyncBaseDao;
import com.example.fortune.dao.sync.delegate.user.UserDao;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository("postgre11UserDao")
@Profile({"sync", "Postgre11"})
public class Postgre11UserDao extends FortuneSyncBaseDao implements UserDao {


}
