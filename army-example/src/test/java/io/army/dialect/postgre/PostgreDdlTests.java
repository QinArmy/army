package io.army.dialect.postgre;

import io.army.dialect._DialectUtils;
import io.army.dialect._MockDialects;
import io.army.mapping.PostgreFullType;
import io.army.mapping.PostgreFullType_;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.modelgen._MetaBridge;
import io.army.schema._FieldResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class PostgreDdlTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreDdlTests.class);

    private final PostgreDdlParser ddlParser = PostgreDdlParser.create((PostgreParser) _MockDialects.from(PostgreDialect.POSTGRE15));

    @Test
    public void createTable() {

        final List<String> sqlList = new ArrayList<>();
        ddlParser.createTable(PostgreFullType_.T, sqlList);

        if (ddlParser.errorMsgList().size() > 0) {
            throw new MetaException(_MetaBridge.createErrorMessage("meta error", ddlParser.errorMsgList()));
        }


        LOG.debug(_DialectUtils.printDdlSqlList(sqlList));

    }


    @Test
    public void addColumn() {
        final List<String> sqlList = new ArrayList<>();

        final List<FieldMeta<?>> fieldList = new ArrayList<>();
        for (FieldMeta<PostgreFullType> field : PostgreFullType_.T.fieldList()) {
            if (_MetaBridge.isReserved(field.fieldName())) {
                continue;
            }

            fieldList.add(field);

        }

        ddlParser.addColumn(fieldList, sqlList);

        if (ddlParser.errorMsgList().size() > 0) {
            throw new MetaException(_MetaBridge.createErrorMessage("meta error", ddlParser.errorMsgList()));
        }

        LOG.debug(_DialectUtils.printDdlSqlList(sqlList));

    }

    @Test
    public void modifyColumn() {

        final List<_FieldResult> resultList = new ArrayList<>();
        _FieldResult result;
        for (FieldMeta<PostgreFullType> field : PostgreFullType_.T.fieldList()) {
            if (_MetaBridge.isReserved(field.fieldName())) {
                continue;
            }
            result = _FieldResult.builder()
                    .field(field)
                    .defaultExp(true)
                    .sqlType(true)
                    .nullable(true)
                    .comment(true)
                    .build();

            resultList.add(result);

        }

        final List<String> sqlList = new ArrayList<>();

        ddlParser.modifyColumn(resultList, sqlList);
        if (ddlParser.errorMsgList().size() > 0) {
            throw new MetaException(_MetaBridge.createErrorMessage("meta error", ddlParser.errorMsgList()));
        }

        LOG.debug(_DialectUtils.printDdlSqlList(sqlList));

    }


}
