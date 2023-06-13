package io.army.session;

import io.army.ArmyException;
import io.army.criteria.BatchDmlStatement;
import io.army.criteria.impl.inner._MultiDml;
import io.army.criteria.impl.inner._SingleDml;
import io.army.criteria.impl.inner._Statement;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

public abstract class _ArmySession implements Session {


    protected final String name;

    protected final boolean readonly;

    protected _ArmySession(String name, boolean readonly) {
        assert _StringUtils.hasText(name);
        this.name = name;
        this.readonly = readonly;
    }


    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final boolean isReadonlySession() {
        return this.readonly;
    }


    protected static int restSecond(final ChildTableMeta<?> domainTable, final long startTime, final int timeout) {
        final int restSeconds;
        final long restMills;
        if (timeout == 0) {
            restSeconds = 0;
        } else if ((restMills = (timeout * 1000L) - (System.currentTimeMillis() - startTime)) < 1L) {
            String m;
            m = String.format("%s first statement completion,but timeout,so no time insert child or update parent.",
                    domainTable);
            throw new ChildUpdateException(m, _Exceptions.timeout(timeout, restMills));
        } else if ((restMills % 1000L) == 0) {
            restSeconds = (int) (restMills / 1000L);
        } else {
            restSeconds = (int) (restMills / 1000L) + 1;
        }
        return restSeconds;
    }


    @Nullable
    protected static TableMeta<?> getBatchUpdateDomainTable(final BatchDmlStatement statement) {
        final TableMeta<?> domainTable;
        if (statement instanceof _MultiDml || statement instanceof _Statement._WithDmlSpec) {
            domainTable = null;
        } else if (statement instanceof _Statement._ChildStatement) {
            domainTable = ((_Statement._ChildStatement) statement).table();
            assert domainTable instanceof ChildTableMeta;
        } else {
            domainTable = ((_SingleDml) statement).table();
        }
        return domainTable;
    }

    protected static ArmyException updateChildNoTransaction() {
        return new ArmyException("update/delete child must in transaction.");
    }


}
