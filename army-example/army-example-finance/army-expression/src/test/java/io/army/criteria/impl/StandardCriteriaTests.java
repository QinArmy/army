package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.example.finance.domain.User;
import io.army.example.finance.domain.User_;
import io.army.example.struct.IdentityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class StandardCriteriaTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardCriteriaTests.class);

    @Test
    public void valueInsert() {
        final Insert insert;
        insert = SQLs.standardValueInsert(User_.T)
                .insertInto(User_.T)
                .values(this::createUserList)
                .asInsert();

        LOG.debug("insert user sql:\n{}", insert);
    }

    private List<User> createUserList() {
        User user;

        user = new User();
        user.setNickName("zoro");
        user.setIdentityType(IdentityType.PERSON);

        return Collections.singletonList(user);
    }

}
