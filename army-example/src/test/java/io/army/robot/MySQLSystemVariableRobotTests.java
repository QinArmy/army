package io.army.robot;

import io.army.util._Collections;
import io.army.util._StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Locale;


/**
 * see {@code io.army.criteria.impl.MySQLExpressions#systemVariable(MySQLs.VarScope, String) }
 */
public class MySQLSystemVariableRobotTests {


    private static final Logger LOG = LoggerFactory.getLogger(MySQLSystemVariableRobotTests.class);


    private static final int GLOBAL_SESSION_SCOPE = 0, SESSION_SCOPE = 1, GLOBAL_SCOPE = 2;

    private static final int BOOLEAN_TYPE = 0, STRING_TYPE = 1, INTEGER_TYPE = 2, DECIMAL_TYPE = 3;


    private static final String TWO_TAB = "\t\t", THREE_TAB = "\t\t\t", FOUR_TAB = "\t\t\t\t", FIVE_TAB = "\t\t\t\t\t";


    private static final String THROW_SCOPE_ERROR = _StringUtils.builder()
            .append(FIVE_TAB)
            .append("throw systemVariableScopeError(scope, name);\n")
            .append(FOUR_TAB)
            .append("}\n")
            .toString();


    @Test
    public void test() throws Exception {
        final Document document;
        document = Jsoup.connect("https://dev.mysql.com/doc/refman/8.3/en/server-system-variables.html").get();

        final Elements tables;
        tables = document.select("table[frame=\"box\"]");

        final String variableNameHeader = "System Variable", typeHeader = "Type", scopeHeader = "Scope";


        final StringBuilder builder = new StringBuilder(4096);

        final List<Triple> tripleList = _Collections.arrayList(333);


        String title, varName, varType, varScope;
        Elements columnList;
        int scope, type;
        for (Element tableElement : tables) {
            varScope = varType = varName = null;

            for (Element row : tableElement.select("tr")) {
                columnList = row.children();
                assert columnList.size() == 2;

                title = columnList.get(0).text();
                switch (title) {
                    case variableNameHeader:
                        varName = columnList.get(1).text();
                        break;
                    case typeHeader:
                        varType = columnList.get(1).text();
                        break;
                    case scopeHeader:
                        varScope = columnList.get(1).text();
                        break;
                }
            }

            assert varName != null;
            assert varScope != null;


            switch (varScope) {
                case "Global":
                    scope = GLOBAL_SCOPE;
                    break;
                case "Global, Session":
                    scope = GLOBAL_SESSION_SCOPE;
                    break;
                case "Session":
                    scope = SESSION_SCOPE;
                    break;
                default:
                    throw new IllegalArgumentException(String.format("unknown var scope %s", varScope));


            } // switch


            if (varType == null) {
                type = STRING_TYPE;
            } else switch (varType) {
                case "Boolean":
                    type = BOOLEAN_TYPE;
                    break;
                case "String":
                case "Set":
                case "Enumeration":
                case "File name":
                case "Directory name":
                    type = STRING_TYPE;
                    break;
                case "Integer":
                    type = INTEGER_TYPE;
                    break;
                case "Numeric":
                    type = DECIMAL_TYPE;
                    break;
                default:
                    throw new IllegalArgumentException(String.format("unknown var type %s", varType));
            } // switch


            builder.append("case \"")
                    .append(varName.toLowerCase(Locale.ROOT))
                    .append("\":");

            tripleList.add(new Triple(scope, builder.toString(), type));

            builder.setLength(0); // clear

        } // loop for


        tripleList.sort(Triple::compareTo);

        builder.setLength(0); // clear

        builder.append(TWO_TAB)
                .append("switch (name.toLowerCase(Locale.ROOT)){\n");

        final int tripleSize = tripleList.size(), lastIndex = tripleSize - 1;
        Triple triple;
        int preScope = -1, preType = -1;

        for (int i = 0; i < tripleSize; i++) {
            triple = tripleList.get(i);

            if (i > 0 && (preScope != triple.scope || preType != triple.type)) {
                appendAction(preScope, preType, builder);
            } else if (i > 0) {
                builder.append('\n');
            }

            builder.append(THREE_TAB)
                    .append(triple.caseCause);
            preScope = triple.scope;
            preType = triple.type;

            if (i == lastIndex) {
                appendAction(triple.scope, triple.type, builder);
            }

        } // loop for


        builder.append(THREE_TAB)
                .append(" default:\n")
                .append(FOUR_TAB)
                .append("type = StringType.INSTANCE;\n")
                .append(TWO_TAB)
                .append("}\n\n");


        LOG.debug("{}", builder);


    }


    private void appendAction(final int scope, final int type, final StringBuilder builder) {

        builder.append(" {\n")
                .append(FOUR_TAB)
                .append("if ( ");
        switch (scope) {
            case GLOBAL_SESSION_SCOPE:
                builder.append("scope != MySQLs.SESSION && scope != MySQLs.GLOBAL ){\n")
                        .append(THROW_SCOPE_ERROR);
                break;
            case SESSION_SCOPE:
                builder.append("scope != MySQLs.SESSION ){\n")
                        .append(THROW_SCOPE_ERROR);
                break;
            case GLOBAL_SCOPE:
                builder.append("scope != MySQLs.GLOBAL ){\n")
                        .append(THROW_SCOPE_ERROR);
                break;
            default:
                throw new IllegalArgumentException();
        }

        builder.append(FOUR_TAB);
        switch (type) {
            case BOOLEAN_TYPE:
                builder.append("type = BooleanType.INSTANCE;\n");
                break;
            case STRING_TYPE:
                builder.append("type = StringType.INSTANCE;\n");
                break;
            case INTEGER_TYPE:
                builder.append("type = IntegerType.INSTANCE;\n");
                break;
            case DECIMAL_TYPE:
                builder.append("type = BigDecimalType.INSTANCE;\n");
                break;
            default:
                throw new IllegalArgumentException();
        }

        builder.append(THREE_TAB)
                .append("}\n")
                .append(THREE_TAB)
                .append("break;\n");
    }


    private static final class Triple implements Comparable<Triple> {

        private final int scope;

        private final String caseCause;

        private final int type;

        private Triple(int scope, String caseCause, int type) {
            this.scope = scope;
            this.caseCause = caseCause;
            this.type = type;
        }


        @Override
        public int compareTo(@NotNull Triple o) {
            final int result;
            if (this.scope != o.scope) {
                result = this.scope - o.scope;
            } else {
                result = this.type - o.type;
            }
            return result;
        }


    }

}
