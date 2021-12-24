package io.army.dialect;


import io.army.session.FactoryMode;

public abstract class AbstractSql {

    protected static final char[] COMMA = new char[]{' ', ','};

    protected static final char[] EQUAL = new char[]{' ', '='};

    protected static final char[] AND = new char[]{' ', 'A', 'N', 'D'};

    protected static final char[] IS_NULL = new char[]{' ', 'I', 'S', ' ', 'N', 'U', 'L', 'L'};

    protected static final char[] LEFT_BRACKET = new char[]{' ', '('};

    protected static final char[] RIGHT_BRACKET = new char[]{' ', ')'};

    protected static final char[] SET_WORD = new char[]{' ', 'S', 'E', 'T'};

    protected static final char[] SELECT_WORD = new char[]{' ', 'S', 'E', 'L', 'E', 'C', 'T'};

    protected static final char[] WHERE_WORD = new char[]{' ', 'W', 'H', 'E', 'R', 'E'};

    protected static final char[] VALUES_WORD = new char[]{' ', 'V', 'A', 'L', 'U', 'E', 'S'};


    protected static final char[] AS_WORD = new char[]{' ', 'A', 'S'};

    protected static final char[] ON_WORD = new char[]{' ', 'O', 'N'};

    protected static final char[] JOIN_WORD = new char[]{' ', 'J', 'O', 'I', 'N'};


    protected final Dialect dialect;

    protected final boolean sharding;

    protected AbstractSql(Dialect dialect) {
        this.dialect = dialect;
        this.sharding = this.dialect.sessionFactory().factoryMode() != FactoryMode.NO_SHARDING;
    }



}
