package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Hint;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner.mysql._MySQLHint;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLModifier;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.session.Database;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

abstract class MySQLUtils extends CriteriaUtils {

    private MySQLUtils() {
    }

    static Dialect defaultDialect(Statement statement) {
        return statement instanceof _MySQLWithClause ? Dialect.MySQL80 : Dialect.MySQL57;
    }

    static void validateDialect(Statement statement, Dialect dialect) {
        if (dialect.database() != Database.MySQL) {
            throw _Exceptions.stmtDontSupportDialect(dialect);
        }
        if (statement instanceof _MySQLWithClause && dialect.version() < Dialect.MySQL80.version()) {
            throw _Exceptions.stmtDontSupportDialect(dialect);
        }
    }

    static List<_MySQLHint> asHintList(List<Hint> hintList) {
        final List<_MySQLHint> mySqlHintList;
        switch (hintList.size()) {
            case 0:
                mySqlHintList = Collections.emptyList();
                break;
            case 1: {
                final Hint hint = hintList.get(0);
                if (!(hint instanceof MySQLHints)) {
                    throw illegalHint(hint);
                }
                mySqlHintList = Collections.singletonList((_MySQLHint) hint);
            }
            break;
            default: {
                final List<_MySQLHint> tempList = new ArrayList<>(hintList.size());
                for (Hint hint : hintList) {
                    if (!(hint instanceof MySQLHints)) {
                        throw illegalHint(hint);
                    }
                    tempList.add((_MySQLHint) hint);
                }
                mySqlHintList = Collections.unmodifiableList(tempList);
            }

        }
        return mySqlHintList;
    }

    static CriteriaException limitParamError() {
        String m = String.format("MySQL limit clause only support %s and %s."
                , Long.class.getName(), Integer.class.getName());
        return new CriteriaException(m);
    }


    static Set<MySQLModifier> asUpdateModifierSet(Set<MySQLModifier> modifierSet) {
        return CriteriaUtils.asModifierSet(modifierSet, MySQLUtils::assertUpdateModifier);
    }


    static CriteriaException indexListIsEmpty() {
        return new CriteriaException("index list must not empty.");
    }


    static CriteriaException illegalHint(@Nullable Hint hint) {
        String m = String.format("%s[%s] isn't %s type."
                , Hint.class.getName(), _ClassUtils.safeClassName(hint), MySQLHints.class.getName());
        throw new CriteriaException(m);
    }


    private static void assertUpdateModifier(final MySQLModifier modifier) {
        switch (modifier) {
            case LOW_PRIORITY:
            case IGNORE:
                break;
            default: {
                String m = String.format("MySQL update syntax don't support %s .", modifier.name());
                throw new CriteriaException(m);
            }
        }
    }


}
