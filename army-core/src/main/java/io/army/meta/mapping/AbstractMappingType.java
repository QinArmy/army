package io.army.meta.mapping;

import java.sql.SQLException;

public abstract class AbstractMappingType<T> extends MappingSupport implements MappingType<T> {

    @Override
    public final Object toSql(T t) {
        if (t == null) {
            return null;
        }
        return nonNullToSql(t);
    }

    @Override
    public final T toJava(Object databaseValue) throws SQLException {
        if (databaseValue == null) {
            return null;
        }
        return nonNullToJava(databaseValue);
    }


    /*######################## template method ################################*/

    protected abstract Object nonNullToSql(T t);

    protected abstract T nonNullToJava(Object databaseValue) throws SQLException;
}
