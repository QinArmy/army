package io.army.criteria.impl;

import io.army.criteria.TypeDef;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._SqlContext;
import io.army.sqltype.DataType;

public abstract class TypeDefs implements TypeDef {

    final DataType dataType;

    private TypeDefs(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public final String typeName() {
        return this.dataType.typeName();
    }

    private static final class TypeDefLength extends TypeDefs implements _SelfDescribed {

        private final int length;

        private TypeDefLength(DataType dataType, int length) {
            super(dataType);
            this.length = length;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

        }

        @Override
        public String toString() {
            return super.toString();
        }


    } // TypeDefLength

}
