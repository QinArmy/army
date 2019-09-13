package io.army.dialect;


/**
 * this class is a  {@link Dialect} implementation and represent Postgre 11.x  Dialect
 * created  on 2018/10/21.
 */
public class Postgre11Dialect extends PostgreDialect {

    public static final Postgre11Dialect INSTANCE = new Postgre11Dialect();

    @Override
    protected String columnFormat() {
        return null;
    }

    @Override
    public Func func() {
        return null;
    }
}
