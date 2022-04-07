package io.army.criteria.impl;

import io.army.criteria.Hint;
import io.army.criteria.Update;
import io.army.criteria.impl.inner._SingleUpdate;

public abstract class _MySQLCounselor extends _SQLCounselor {


    public static void assertUpdate(final Update update) {
        if (update instanceof _SingleUpdate) {
            if (!(update instanceof MySQLSingleUpdate)) {
                throw instanceNotMatch(update, MySQLSingleUpdate.class);
            }
        } else if (!(update instanceof MySQLMultiUpdate)) {
            throw instanceNotMatch(update, MySQLMultiUpdate.class);
        }

    }

    public static void assertHint(Hint hint) {
        if (!(hint instanceof MySQLHints)) {
            throw MySQLUtils.illegalHint(hint);
        }
    }


}
