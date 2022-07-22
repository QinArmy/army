package io.army.stmt;

import io.army.criteria.NamedParam;
import io.army.criteria.SqlParam;
import io.army.criteria.SqlValueParam;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.Collection;
import java.util.List;

abstract class SqlParams implements SqlParam {


    static SingleParam single(ParamMeta paramMeta, @Nullable Object value) {
        return new NullableSingleParam(paramMeta, value);
    }

    static MultiParam multi(final NamedParam.NamedMulti namedParam, final Collection<?> values) {
        final List<?> valueList;
        valueList = _CollectionUtils.asUnmodifiableList(values);
        if (valueList.size() != namedParam.valueSize()) {
            throw _Exceptions.namedMultiParamSizeError(namedParam, values.size());
        }
        return new SqlMultiParam(namedParam.paramMeta(), valueList);
    }


    private final ParamMeta paramMeta;

    private SqlParams(ParamMeta paramMeta) {
        this.paramMeta = paramMeta;
    }


    @Override
    public final ParamMeta paramMeta() {
        return this.paramMeta;
    }


    private static final class NullableSingleParam extends SqlParams
            implements SingleParam, SqlValueParam.SingleValue {

        private final Object value;

        private NullableSingleParam(ParamMeta paramMeta, @Nullable Object value) {
            super(paramMeta);
            this.value = value;
        }

        @Override
        public Object value() {
            return this.value;
        }

    }//NonNullSingleParam


    private static final class SqlMultiParam extends SqlParams
            implements MultiParam, SqlValueParam.MultiValue {

        private final List<?> valueList;

        private SqlMultiParam(ParamMeta paramMeta, List<?> valueList) {
            super(paramMeta);
            assert valueList.size() > 0;
            this.valueList = valueList;
        }

        @Override
        public int valueSize() {
            return this.valueList.size();
        }

        @Override
        public List<?> valueList() {
            return this.valueList;
        }


    }//NonNullMultiParam


}
