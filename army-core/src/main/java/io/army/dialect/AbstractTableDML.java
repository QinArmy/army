package io.army.dialect;

import com.sun.xml.internal.bind.v2.model.core.ID;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.time.ZoneId;

public abstract class AbstractTableDML implements TableDML {

    private final SQL sql;

    public AbstractTableDML(SQL sql) {
        this.sql = sql;
    }

    /*################################## blow SQL interface method ##################################*/

    @Override
    public final String quoteIfNeed(String identifier) {
        return sql.quoteIfNeed(identifier);
    }

    @Override
    public final boolean isKeyWord(String identifier) {
        return sql.isKeyWord(identifier);
    }

    @Override
    public final ZoneId zoneId() {
        return sql.zoneId();
    }

    /*################################## blow TableDML method ##################################*/

    @Override
    public final SQLWrapper insert(TableMeta<?> tableMeta, IDomain entity) {
        Assert.notNull(tableMeta, "tableMeta required");
        Assert.notNull(entity, "entity required");
        Assert.isTrue(tableMeta.javaType() == entity.getClass(), "tableMata and entity not match");

        SQLWrapper wrapper;
        if (tableMeta.parent() == null) {
            wrapper = createInsertForSimple(tableMeta, entity);
        } else {
            wrapper = createInsertForChild(tableMeta, entity);
        }
        return wrapper;
    }


    /*################################## blow protected template method ##################################*/

    /*################################## blow protected method ##################################*/

    /*################################## blow private method ##################################*/

    private SQLWrapper createInsertForSimple(TableMeta<?> tableMeta, IDomain entity) {
        for (FieldMeta<?, ?> fieldMeta : tableMeta.fieldCollection()) {

        }
        return null;
    }

    private SQLWrapper createInsertForChild(TableMeta<?> tableMeta, IDomain entity) {
        return null;
    }

}
