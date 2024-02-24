package io.army.robot;


import com.alibaba.druid.pool.DruidDataSource;
import io.army.dialect.Database;
import io.army.session.DataSourceUtils;
import io.army.util._Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;

public class PostgreVariableRobotTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreVariableRobotTests.class);

    private static final int BOOLEAN_TYPE = 0, INTEGER_TYPE = 1, FLOAT_TYPE = 2, STRING_TYPE = 4;

    private static final String TWO_TAB = "\t\t", THREE_TAB = "\t\t\t", FOUR_TAB = "\t\t\t\t";


    /**
     * <p>See {@code  PostgreCommands#show(String)}
     */
    @Test(enabled = false)
    public void variableCaseStatement() throws SQLException {
        try (DruidDataSource dataSource = DataSourceUtils.createDataSource(Database.PostgreSQL)) {

            try (Connection conn = dataSource.getConnection()) {

                try (Statement statement = conn.createStatement()) {
                    try (ResultSet resultSet = statement.executeQuery("SELECT t.* FROM pg_settings AS t ")) {
                        readShowAll(resultSet);
                    }
                }
            }

        }

    }


    private void readShowAll(final ResultSet resultSet) throws SQLException {
        final List<Pair> pairList = _Collections.arrayList(355);
        String name, varType;
        int type;
        while (resultSet.next()) {
            name = resultSet.getString("name");
            varType = resultSet.getString("vartype");
            switch (varType) {
                case "bool":
                    type = BOOLEAN_TYPE;
                    break;
                case "integer":
                    type = INTEGER_TYPE;
                    break;
                case "real":
                    type = FLOAT_TYPE;
                    break;
                case "enum":
                case "string":
                    type = STRING_TYPE;
                    break;
                default:
                    throw new IllegalArgumentException(String.format("unknown type %s", varType));

            } // switch

            pairList.add(new Pair(name, type));

        }

        pairList.sort(Pair::compareTo);

        final StringBuilder builder = new StringBuilder(4096);


        builder.append(TWO_TAB)
                .append("switch (name.toLowerCase(Locale.ROOT)){\n");

        final int pairSize = pairList.size();
        Pair pair;
        int preType = -1;

        for (int i = 0; i < pairSize; i++) {
            pair = pairList.get(i);

            if (i > 0 && preType != pair.type) {
                appendAction(preType, builder);
            } else if (i > 0) {
                builder.append('\n');
            }

            builder.append(THREE_TAB)
                    .append("case \"")
                    .append(pair.name.toLowerCase(Locale.ROOT))
                    .append("\":");
            preType = pair.type;


        } // loop for


        builder.append('\n')
                .append(THREE_TAB)
                .append(" default:\n")
                .append(FOUR_TAB)
                .append("type = StringType.INSTANCE;\n")
                .append(TWO_TAB)
                .append("}\n\n");


        LOG.debug("{}", builder);


    }


    private void appendAction(final int type, final StringBuilder builder) {

        builder.append('\n')
                .append(FOUR_TAB);
        switch (type) {
            case BOOLEAN_TYPE:
                builder.append("type = BooleanType.INSTANCE;\n");
                break;
            case INTEGER_TYPE:
                builder.append("type = IntegerType.INSTANCE;\n");
                break;
            case FLOAT_TYPE:
                builder.append("type = FloatType.INSTANCE;\n");
                break;
            case STRING_TYPE:
                builder.append("type = StringType.INSTANCE;\n");
                break;
            default:
                throw new IllegalArgumentException();
        }

        builder.append(THREE_TAB)
                .append("break;\n");


    }


    private static boolean isDecimal(String value) {
        boolean match;
        try {
            new BigDecimal(value);
            match = true;
        } catch (NumberFormatException e) {
            match = false;
        }
        return match;
    }


    private static final class Pair implements Comparable<Pair> {

        private final String name;

        private final int type;

        private Pair(String name, int type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public int compareTo(PostgreVariableRobotTests.Pair o) {
            final int result;
            if (this.type != o.type) {
                result = this.type - o.type;
            } else {
                result = this.name.compareTo(o.name);
            }
            return result;
        }


    } // Pair


}
