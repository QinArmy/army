package io.army.dialect.mysql;

import io.army.criteria.*;
import io.army.criteria.impl._MySQLCounselor;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner.mysql._MySQLSingleUpdate;
import io.army.criteria.mysql.MySQLModifier;
import io.army.dialect.*;
import io.army.meta.SingleTableMeta;
import io.army.stmt.SimpleStmt;
import io.army.util._Exceptions;

import java.util.List;

class MySQLDialect extends MySQL {

    static final String SPACE_HINT_START = " /*+";

    static final String SPACE_HINT_END = " */";

    MySQLDialect(_DialectEnvironment environment, Dialect dialect) {
        super(environment, dialect);
    }

    @Override
    protected final void assertDialectInsert(Insert insert) {
        super.assertDialectInsert(insert);
    }

    @Override
    protected final void assertDialectUpdate(Update update) {
        _MySQLCounselor.assertUpdate(update);
    }

    @Override
    protected final void assertDialectDelete(Delete delete) {
        super.assertDialectDelete(delete);
    }

    @Override
    protected final void assertDialectQuery(Query query) {
        super.assertDialectQuery(query);
    }


    @Override
    protected SimpleStmt dialectSingleUpdate(final _SingleUpdateContext context, final _SingleUpdate update) {
        assert context.childBlock() == null;
        final _MySQLSingleUpdate stmt = (_MySQLSingleUpdate) update;
        final StringBuilder sqlBuilder = context.sqlBuilder()
                //1. UPDATE key word
                .append(Constant.UPDATE);

        //2. hint comment block
        final List<Hint> hintList = stmt.hintList();
        if (hintList.size() > 0) {
            sqlBuilder.append(SPACE_HINT_START);
            for (Hint hint : hintList) {
                _MySQLCounselor.assertHint(hint);
                sqlBuilder.append(Constant.SPACE)
                        .append(hint);
            }
            sqlBuilder.append(SPACE_HINT_END);
        }
        //3. modifier
        for (SQLModifier modifier : stmt.modifierList()) {
            if (!(modifier instanceof MySQLModifier)) {
                throw _Exceptions.dialectAndModifierNotMatch(this.dialect, modifier);
            }
            switch ((MySQLModifier) modifier) {
                case LOW_PRIORITY:
                case IGNORE:
                    sqlBuilder.append(modifier.render());
                    break;
                default:
                    throw _Exceptions.commandAndModifierNotMatch(stmt, modifier);

            }
        }

        //4. table name
        final _Dialect dialect = context.dialect();
        final SingleTableMeta<?> table = context.table();
        sqlBuilder.append(Constant.SPACE)
                .append(dialect.quoteIfNeed(table.tableName()))
        ;
        stmt.indexHintList();
        return context.build();
    }


}
