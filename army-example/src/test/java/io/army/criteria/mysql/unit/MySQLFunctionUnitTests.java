package io.army.criteria.mysql.unit;

import io.army.criteria.PrimaryStatement;
import io.army.criteria.Select;
import io.army.criteria.Visible;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.dialect.mysql.MySQLDialect;
import io.army.example.pill.domain.PillUser_;
import io.army.example.pill.struct.PillUserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.impl.SQLs.AS;

public class MySQLFunctionUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLFunctionUnitTests.class);

    @Test
    public void nthValue() {

    }

    @Test
    public void caseFunc() {
        final Select stmt;
        stmt = MySQLs.query()
                .select(MySQLs.cases(PillUser_.userType)
                        .when(SQLs.literalFrom(PillUserType.NONE))
                        .then(SQLs.literalFrom(1))

                        .when(SQLs.literalFrom(PillUserType.PARTNER))
                        .then(SQLs.literalFrom(2))

                        .when(SQLs.literalFrom(PillUserType.ENTERPRISE))
                        .then(SQLs.literalFrom(3))

                        .elseValue(SQLs.literalFrom(0))

                        .end()
                        .plus(SQLs::literal, 1).as("userType"))
                .from(PillUser_.T, AS, "u")
                .asQuery();
        printStmt(stmt);
    }


    private void printStmt(final PrimaryStatement statement) {
        String sql;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            sql = statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            LOG.debug("{}:\n{}", dialect.name(), sql);
        }

    }


}
