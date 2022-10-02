package io.army.criteria.postgre;

import io.army.criteria.CteBuilderSpec;

public interface PostgreCteBuilder extends CteBuilderSpec {

    PostgreInsert._DynamicSubInsert<Void> cteInsert(String name);

    <C> PostgreInsert._DynamicSubInsert<C> cteInsert(C criteria, String name);


}
