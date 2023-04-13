package io.army.criteria.impl;

import io.army.mapping.BigDecimalType;
import io.army.mapping.DoubleType;
import io.army.mapping.MappingType;

abstract class ExpTypes {

    private ExpTypes() {
        throw new UnsupportedOperationException();
    }


    static MappingType numberExpType(final MappingType left, final MappingType right) {
        final MappingType returnType;
        if (left instanceof MappingType.SqlFloatType || right instanceof MappingType.SqlFloatType) {
            returnType = DoubleType.INSTANCE;
        } else if (left instanceof MappingType.SqlDecimalType || right instanceof MappingType.SqlDecimalType) {
            returnType = BigDecimalType.INSTANCE;
        } else if (!(left instanceof MappingType.SqlIntegerType && right instanceof MappingType.SqlIntegerType)) {
            returnType = left;
        } else if (((MappingType.SqlIntegerType) left).lengthType()
                .compareWith(((MappingType.SqlIntegerType) right).lengthType()) >= 0) {
            returnType = left;
        } else {
            returnType = right;
        }
        return returnType;
    }


}
