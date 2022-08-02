package io.army.criteria.impl;


import io.army.criteria.DataField;
import io.army.criteria.IPredicate;
import io.army.criteria.Statement;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

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
     public final OR on(Consumer<Consumer<IPredicate>> consumer) {
         return this.stmt;
     }

     @Override
     public final OR on(BiConsumer<C, Consumer<IPredicate>> consumer) {
         return this.stmt;
     }


 }
