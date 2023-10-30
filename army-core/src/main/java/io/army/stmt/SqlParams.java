package io.army.stmt;

import io.army.criteria.NamedParam;
import io.army.criteria.SQLParam;
import io.army.criteria.SqlValueParam;

import javax.annotation.Nullable;

import io.army.meta.TypeMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.Collection;
import java.util.List;

abstract class SqlParams implements SQLParam {


    static SingleParam single(TypeMeta paramMeta, @Nullable Object value) {
        return new NullableSingleParam(paramMeta, value);
    }

    static MultiParam multi(final NamedParam.NamedRow namedParam, final Collection<?> values) {
        final List<?> valueList;
        valueList = _Collections.asUnmodifiableList(values);
        if (valueList.size() != namedParam.columnSize()) {
            throw _Exceptions.namedMultiParamSizeError(namedParam, values.size());
        }
        return new SqlMultiParam(namedParam.typeMeta(), valueList);
    }


    private final TypeMeta paramMeta;

    private SqlParams(TypeMeta paramMeta) {
        this.paramMeta = paramMeta;
    }


    @Override
    public final TypeMeta typeMeta() {
        return this.paramMeta;
    }


    private static final class NullableSingleParam extends SqlParams
            implements SingleParam, SqlValueParam.SingleValue {

        private final Object value;

        private NullableSingleParam(TypeMeta paramMeta, @Nullable Object value) {
            super(paramMeta);
            this.value = value;
        }

        @Override
        public Object value() {
            return this.value;
        }

    }//NonNullSingleParam


    private static final class SqlMultiParam extends SqlParams
            implements MultiParam {

        private final List<?> valueList;

        private SqlMultiParam(TypeMeta paramMeta, List<?> valueList) {
            super(paramMeta);
            assert valueList.size() > 0;
            this.valueList = valueList;
        }

        @Override
        public int columnSize() {
            return this.valueList.size();
        }

        @Override
        public List<?> valueList() {
            return this.valueList;
        }


    }//NonNullMultiParam


}
