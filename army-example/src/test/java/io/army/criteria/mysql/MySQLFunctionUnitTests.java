package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.dialect.mysql.MySQLDialect;
import io.army.example.pill.domain.PillUser_;
import io.army.example.pill.struct.UserType;
import io.army.mapping.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.function.Consumer;

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
                .select(this::simpleCaseFunc)
                .from(PillUser_.T, AS, "u")
                .asQuery();
        printStmt(stmt);
    }

    /**
     * @see #caseFunc()
     */
    private void simpleCaseFunc(Consumer<SelectItem> consumer) {
        Selection selection;
        selection = MySQLs.Case(PillUser_.userType)
                .when(SQLs.literalFrom(UserType.NONE))
                .then(SQLs.literalFrom(1))

                .when(SQLs.literalFrom(UserType.PARTNER))
                .then(SQLs.literalFrom(2))

                .when(SQLs.literalFrom(UserType.ENTERPRISE))
                .then(SQLs.literalFrom(3))

                .Else(SQLs.literalFrom(0))

                .end()

                .asType(StringType.INSTANCE)
                .as("result");

        consumer.accept(selection);
    }

    private void printStmt(final PrimaryStatement statement) {
        String sql;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            sql = statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            LOG.debug("{}:\n{}", dialect.name(), sql);
        }

    }


}
