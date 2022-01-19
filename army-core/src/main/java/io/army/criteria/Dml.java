package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Deprecated
public interface Dml extends Statement {

    interface DmlSpec<D extends Dml> {

        D asDml();
    }


    interface WhereAndSpec<C, D extends Dml> extends DmlSpec<D> {

        WhereAndSpec<C, D> and(IPredicate predicate);

        WhereAndSpec<C, D> and(Function<C, IPredicate> function);

        WhereAndSpec<C, D> and(Supplier<IPredicate> supplier);

        /**
         * @see Expression#ifEqual(Object)
         */
        WhereAndSpec<C, D> ifAnd(@Nullable IPredicate predicate);

        WhereAndSpec<C, D> ifAnd(Function<C, IPredicate> function);

        WhereAndSpec<C, D> ifAnd(Supplier<IPredicate> supplier);

    }


    interface BatchWhereAndSpec<C, D extends Dml> extends BatchParamSpec<C, D> {

        /**
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         * @see SQLs#nullableNamedParam(GenericField)
         * @see SQLs#nullableNamedParam(String, ParamMeta)
         */
        BatchWhereAndSpec<C, D> and(IPredicate predicate);

        /**
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         * @see SQLs#nullableNamedParam(GenericField)
         * @see SQLs#nullableNamedParam(String, ParamMeta)
         */
        BatchWhereAndSpec<C, D> and(Supplier<IPredicate> supplier);

        /**
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         * @see SQLs#nullableNamedParam(GenericField)
         * @see SQLs#nullableNamedParam(String, ParamMeta)
         */
        BatchWhereAndSpec<C, D> and(Function<C, IPredicate> function);

        /**
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         * @see SQLs#nullableNamedParam(GenericField)
         * @see SQLs#nullableNamedParam(String, ParamMeta)
         * @see Expression#ifEqual(Object)
         */
        BatchWhereAndSpec<C, D> ifAnd(@Nullable IPredicate predicate);

        /**
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         * @see SQLs#nullableNamedParam(GenericField)
         * @see SQLs#nullableNamedParam(String, ParamMeta)
         */
        BatchWhereAndSpec<C, D> ifAnd(Supplier<IPredicate> supplier);

        /**
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         * @see SQLs#nullableNamedParam(GenericField)
         * @see SQLs#nullableNamedParam(String, ParamMeta)
         */
        BatchWhereAndSpec<C, D> ifAnd(Function<C, IPredicate> function);

    }


    interface BatchParamSpec<C, D extends Dml> {

        DmlSpec<D> paramMaps(List<Map<String, Object>> mapList);

        DmlSpec<D> paramMaps(Supplier<List<Map<String, Object>>> supplier);

        DmlSpec<D> paramMaps(Function<C, List<Map<String, Object>>> function);

        DmlSpec<D> paramBeans(List<Object> beanList);

        DmlSpec<D> paramBeans(Supplier<List<Object>> supplier);

        DmlSpec<D> paramBeans(Function<C, List<Object>> function);

    }


}
