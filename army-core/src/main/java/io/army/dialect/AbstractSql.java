package io.army.dialect;


public abstract class AbstractSql {

    protected static final char[] COMMA = new char[]{' ', ','};

    protected static final char[] EQUAL = new char[]{' ', '='};

    protected static final char[] AND = new char[]{' ', 'A', 'N', 'D'};

    protected static final char[] IS_NULL = new char[]{' ', 'I', 'S', ' ', 'N', 'U', 'L', 'L'};

    protected static final char[] LEFT_BRACKET = new char[]{' ', '('};

    protected static final char[] RIGHT_BRACKET = new char[]{' ', ')'};

    protected static final char[] SET_WORD = new char[]{' ', 'S', 'E', 'T'};



    protected final DialectEnvironment environment;


    protected AbstractSql(DialectEnvironment environment) {
        this.environment = environment;

    }


}
