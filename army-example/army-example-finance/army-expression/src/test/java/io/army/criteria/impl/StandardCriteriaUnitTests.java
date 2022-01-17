package io.army.criteria.impl;

import io.army.criteria.Insert;
import io.army.example.finance.domain.Person;
import io.army.example.finance.domain.Person_;
import io.army.example.finance.domain.User;
import io.army.example.finance.domain.User_;
import io.army.example.struct.IdentityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class StandardCriteriaUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardCriteriaUnitTests.class);

    @Test
    public void insertParent() {
        final Insert insert;
        insert = SQLs.standardValueInsert(User_.T)
                .insertInto(User_.T)
                .set(User_.identityType, IdentityType.PERSON)
                .values(this::createUserList)
                .asInsert();

        LOG.debug("{}", insert);
    }

    @Test
    public void insertChild() {
        final Insert insert;
        insert = SQLs.standardValueInsert(Person_.T)
                .insertInto(Person_.T)
                .set(Person_.identityType, IdentityType.PERSON)
                .values(this::createPersonList)
                .asInsert();
        System.out.println(insert);
        // LOG.debug("{}", insert);
    }

    private List<User> createUserList() {
        List<User> domainList = new ArrayList<>();
        User user;

        user = new User();
        user.setId(1L);
        user.setNickName("zoro");
        domainList.add(user);

        user = new User();
        user.setId(2L);
        user.setNickName("zoro1");
        domainList.add(user);
        return domainList;
    }

    private List<Person> createPersonList() {
        List<Person> domainList = new ArrayList<>();

        Person user;

        user = new Person();
        user.setId(1L);
        user.setNickName("zoro");
        user.setBirthday(LocalDate.of(1990, Month.APRIL, 1));
        user.setPhone("199000114433");
        domainList.add(user);

        user = new Person();
        user.setId(2L);
        user.setNickName("zoro1");
        user.setBirthday(LocalDate.of(2000, Month.APRIL, 1));
        user.setPhone("199000114433");
        domainList.add(user);

        return domainList;
    }

}
