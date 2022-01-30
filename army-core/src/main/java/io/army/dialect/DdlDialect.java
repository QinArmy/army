package io.army.dialect;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;

import java.util.List;

interface DdlDialect {

    List<String> errorMsgList();


    <T extends IDomain> void createTable(TableMeta<T> table, List<String> sqlList);


}
