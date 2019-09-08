package io.army.dialect;

/**
 * this class is a  {@link Dialect} implementation and abstract base class of all MySQL Dialect
 * created  on 2018/10/21.
 */
public class MySQLDialect extends AbstractDialect implements Dialect {

    public static final MySQLDialect INSTANCE = new MySQLDialect();

    protected MySQLDialect() {
    }


    @Override
    protected String columnFormat() {
        return " %s %s NOT NULL DEFAULT %s common '%s'";
    }
}
