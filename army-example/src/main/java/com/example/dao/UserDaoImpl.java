package com.example.dao;

import com.example.domain.user.User;

/**
 * created  on 2018/11/19.
 */
public class UserDaoImpl implements UserDao {


    @Override
    public User get(Long id) {
/*
        DLS.multiSelect(User_.authStatus, User_.name)
                .from(User.class)
                .join(Person.class).asType("p").on(Person_.id.eq(User_.id))
                .where(User_.id.eq(3L))

        ;*/
        return null;
    }
}
