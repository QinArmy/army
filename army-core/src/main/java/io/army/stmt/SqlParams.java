package io.army.stmt;

import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.util._CollectionUtils;

import java.util.Collection;
import java.util.List;

abstract class SqlParams implements SqlParam {


    static SingleParam single(ParamMeta paramMeta, @Nullable Object value) {
        return value == null ? new NullSingleParam(paramMeta) : new NonNullSingleParam(paramMeta, value);
    }

    static MultiParam multi(final NamedMultiParam namedParam, final Collection<?> values) {
        final List<?> valueList;
        valueList = _CollectionUtils.asUnmodifiableList(values);
        assert valueList.size() == namedParam.size();
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


    private static final class NonNullSingleParam extends SqlParams implements SingleParam {

        private final Object value;

        private NonNullSingleParam(ParamMeta paramMeta, Object value) {
            super(paramMeta);
            this.value = value;
        }

        @Override
        public Object value() {
            return this.value;
        }

    }//NonNullSingleParam

    private static final class NullSingleParam extends SqlParams implements SingleParam {

        private NullSingleParam(ParamMeta paramMeta) {
            super(paramMeta);
        }

        @Override
        public Object value() {
            //always null
            return null;
        }

    }//NullSingleParam


    private static final class SqlMultiParam extends SqlParams implements MultiParam {

        private final List<?> valueList;

        private SqlMultiParam(ParamMeta paramMeta, List<?> valueList) {
            super(paramMeta);
            assert valueList.size() > 0;
            this.valueList = valueList;
        }

        @Override
        public List<?> valueList() {
            return this.valueList;
        }


    }//NonNullMultiParam


}
