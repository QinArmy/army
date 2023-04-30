package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.meta.TableMeta;

public interface ExpressionElementConsumer {

    ExpressionElementConsumer comma(ExpressionElement exp);


    ExpressionElementConsumer comma(String alias, SQLs.SymbolPeriod period, TableMeta<?> table);

    ExpressionElementConsumer comma(String alias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk);


}
