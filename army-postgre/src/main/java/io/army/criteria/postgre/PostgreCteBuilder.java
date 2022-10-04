package io.army.criteria.postgre;

import io.army.criteria.CteBuilderSpec;

public interface PostgreCteBuilder extends CteBuilderSpec {

    PostgreInsert._DynamicSubInsert<Void, PostgreCteBuilder> cteInsert(String name);

    <C> PostgreInsert._DynamicSubInsert<C, PostgreCteBuilder> cteInsert(C criteria, String name);


}
