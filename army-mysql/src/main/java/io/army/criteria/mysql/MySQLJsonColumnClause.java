package io.army.criteria.mysql;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.SQLElement;
import io.army.criteria.SQLIdentifier;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.sqltype.MySQLType;

import java.util.function.Function;

public interface MySQLJsonColumnClause extends Item {

    MySQLFunction._JsonTableDynamicOnEmptyActionSpec column(String name, SQLs.WordsForOrdinality forOrdinality);

    MySQLFunction._JsonTableDynamicOnEmptyActionSpec column(String name, MySQLType type, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

    MySQLFunction._JsonTableDynamicOnEmptyActionSpec column(String name, MySQLType type, Expression n, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

    MySQLFunction._JsonTableDynamicOnEmptyActionSpec column(String name, MySQLType type, int n, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

    MySQLFunction._JsonTableDynamicOnEmptyActionSpec column(String name, MySQLType type, Expression n, SQLElement charset, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

    MySQLFunction._JsonTableDynamicOnEmptyActionSpec column(String name, MySQLType type, int n, SQLElement charset, SQLIdentifier collate, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

    MySQLFunction._JsonTableDynamicOnEmptyActionSpec column(String name, MySQLType type, Expression n, SQLElement charset, SQLIdentifier collate, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

    MySQLFunction._JsonTableDynamicOnEmptyActionSpec column(String name, MySQLType type, int p, int m, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

    MySQLFunction._JsonTableDynamicOnEmptyActionSpec column(String name, MySQLType type, Expression p, Expression m, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

    /*-------------------below exists path -------------------*/


    MySQLJsonColumnClause column(String name, MySQLType type, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

    MySQLJsonColumnClause column(String name, MySQLType type, Expression n, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

    MySQLJsonColumnClause column(String name, MySQLType type, int n, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

    MySQLJsonColumnClause column(String name, MySQLType type, Expression n, SQLElement charset, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

    MySQLJsonColumnClause column(String name, MySQLType type, int n, SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

    MySQLJsonColumnClause column(String name, MySQLType type, Expression n, SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

    MySQLJsonColumnClause column(String name, MySQLType type, int p, int m, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

    MySQLJsonColumnClause column(String name, MySQLType type, Expression p, Expression m, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);


    /*-------------------below nested -------------------*/

    MySQLJsonColumnClause nested(Expression path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function);

    MySQLJsonColumnClause nested(Function<String, Expression> operator, String path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function);

    MySQLJsonColumnClause nestedPath(Expression path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function);

    MySQLJsonColumnClause nestedPath(Function<String, Expression> operator, String path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function);


}
