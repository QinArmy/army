package io.army.criteria;

public interface LongExpression extends NumberExpression<Long> {

    LongExpression and(LongExpression operator);

    LongExpression and(long operator);

    LongExpression or(LongExpression operator);

    LongExpression or(long operator);

    LongExpression xor(LongExpression operator);

    LongExpression xor(long operator);

    LongExpression inversion(LongExpression operator);

    LongExpression inversion(long operator);

    LongExpression rightShift(long  operator);

    LongExpression rightShift(LongExpression operator);

    LongExpression leftShift(long  operator);

    LongExpression leftShift(LongExpression operator);

}
