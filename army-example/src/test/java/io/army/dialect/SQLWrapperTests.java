package io.army.dialect;

import io.army.boot.GenericSessionTests;
import io.army.meta.mapping.MappingFactory;
import io.army.wrapper.ParamWrapper;
import io.army.wrapper.SQLWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SQLWrapperTests {

    private static final Logger LOG = LoggerFactory.getLogger(GenericSessionTests.class);


    @Test
    public void printInsertWrapper() {
        List<ParamWrapper> list = new ArrayList<>(3);

        list.add(ParamWrapper.build(MappingFactory.getDefaultMapping(Long.class), 1L));
        list.add(ParamWrapper.build(MappingFactory.getDefaultMapping(LocalDateTime.class), LocalDateTime.now()));
        list.add(ParamWrapper.build(MappingFactory.getDefaultMapping(Boolean.class), Boolean.TRUE));

        SQLWrapper wrapper = SQLWrapper.build(
                "INSERT INTO a_account(id,create_time,visible) value(?,?,?)"
                , list);
        LOG.info("InsertWrapper:{}", wrapper);

    }
}
