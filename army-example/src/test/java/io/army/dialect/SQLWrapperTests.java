package io.army.dialect;

import io.army.boot.SessionTests;
import io.army.meta.mapping.BooleanMapping;
import io.army.meta.mapping.LocalDateMapping;
import io.army.meta.mapping.LongMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SQLWrapperTests {

    private static final Logger LOG = LoggerFactory.getLogger(SessionTests.class);


    @Test
    public void printInsertWrapper() {
        List<ParamWrapper> list = new ArrayList<>(3);

        list.add(ParamWrapper.build(LongMapping.INSTANCE, 1L));
        list.add(ParamWrapper.build(LocalDateMapping.INSTANCE, LocalDateTime.now()));
        list.add(ParamWrapper.build(BooleanMapping.INSTANCE, Boolean.TRUE));

        SQLWrapper wrapper = SQLWrapper.build(
                "INSERT INTO a_account(id,create_time,visible) value(?,?,?)"
                , list);
        LOG.info("InsertWrapper:{}", wrapper);

    }
}