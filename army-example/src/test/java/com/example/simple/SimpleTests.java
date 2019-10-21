package com.example.simple;

import com.example.domain.account.Account;
import com.example.domain.account.Account_;
import com.example.domain.user.User_;
import io.army.annotation.MappedSuperclass;
import io.army.dialect.mysql.MySQL57Dialect;
import io.army.util.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class SimpleTests {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleTests.class);


    @Test
    public void annotation() {
        MappedSuperclass mappedSuperclass = AnnotationUtils.getAnnotation(Account.class, MappedSuperclass.class);
        LOG.info("mappedSuperclass:{}", mappedSuperclass);

        mappedSuperclass = AnnotationUtils.findAnnotation(Account.class, MappedSuperclass.class);
        LOG.info("mappedSuperclass:{}", mappedSuperclass);
    }

    @Test
    public void domainMeta() {
        LOG.info("load BaseRecord meta {}", Account_.FIELD_COUNT);
    }

    @Test
    public void mysqlDDL() {
        LOG.info("mysql ddl :\n {}", MySQL57Dialect.INSTANCE.tableDefinition(User_.T));
    }
}
