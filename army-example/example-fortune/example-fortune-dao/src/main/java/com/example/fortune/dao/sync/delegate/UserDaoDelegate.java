package com.example.fortune.dao.sync.delegate;

import com.example.fortune.dao.sync.user.UserDao;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository("userDao")
@Primary
public class UserDaoDelegate extends AbstractFortuneDelegateDao<UserDao> implements UserDao {


    @Override
    protected Class<UserDao> getDaoClass() {
        return UserDao.class;
    }
}
