package io.army.criteria.impl;


import io.army.criteria.DataField;
import io.army.criteria.IPredicate;
import io.army.criteria.Statement;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

 class NoActionOnClause<C, OR> implements Statement._OnClause<C, OR> {

     final OR stmt;

     NoActionOnClause(OR stmt) {
         this.stmt = stmt;
     }

     @Override
     public final OR on(IPredicate predicate) {
         return this.stmt;
     }

     @Override
     public final OR on(IPredicate predicate1, IPredicate predicate2) {
         return this.stmt;
     }

     @Override
     public final OR on(Function<Object, IPredicate> operator, DataField operandField) {
         return this.stmt;
     }

     @Override
     public final OR on(Function<Object, IPredicate> operator1, DataField operandField1
             , Function<Object, IPredicate> operator2, DataField operandField2) {
         return this.stmt;
     }

     @Override
     public final OR on(Function<C, List<IPredicate>> function) {
         return this.stmt;
     }

     @Override
     public final OR on(Supplier<List<IPredicate>> supplier) {
         return this.stmt;
     }

    @Override
    public final OR on(Consumer<List<IPredicate>> consumer) {
        return this.stmt;
    }


}
