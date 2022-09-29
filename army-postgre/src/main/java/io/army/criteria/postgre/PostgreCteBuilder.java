package io.army.criteria.postgre;

import io.army.criteria.DialectStatement;

public interface PostgreCteBuilder extends DialectStatement._CteBuilder {

    PostgreInsert._DynamicSubInsert<Void> cteInsert(String name);

    <C> PostgreInsert._DynamicSubInsert<C> cteInsert(C criteria, String name);


}
