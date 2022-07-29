package io.army.criteria.impl;

import io.army.criteria.RowConstructor;
import io.army.criteria.RowSet;
import io.army.criteria.SubValues;
import io.army.criteria.Values;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Values;
import io.army.dialect._SqlContext;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

abstract class SimpleValues<C, V extends RowSet.DqlValues, RR, VR, UR, OR, LR> extends PartRowSet<
        C,
        V,
        Void,
        Void,
        Void,
        Void,
        Void,
        Void,
        UR,
        OR,
        LR,
        Void>
        implements Values._StaticValueLeftParenClause<RR>
        , Values._StaticValueRowCommaDualSpec<RR>
        , Values._StaticValueRowCommaQuadraSpec<RR>
        , Values._ValuesDynamicClause<C, VR>
        , RowSet.DqlValues
        , Values._ValuesSpec<V>
        , _Values {


    SimpleValues(CriteriaContext criteriaContext) {
        super(criteriaContext, JoinableClause.voidClauseSuppler());

        if (this instanceof SubValues) {
            CriteriaContextStack.push(this.criteriaContext);
        } else {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

    }


    @Override
    public final VR values(Consumer<RowConstructor> consumer) {
        final CriteriaSupports.RowConstructorImpl constructor;
        constructor = new CriteriaSupports.RowConstructorImpl(this.criteriaContext);
        consumer.accept(constructor);
        return this.dynamicValuesEnd(constructor.endConstructor());
    }

    @Override
    public final VR values(BiConsumer<C, RowConstructor> consumer) {
        final CriteriaSupports.RowConstructorImpl constructor;
        constructor = new CriteriaSupports.RowConstructorImpl(this.criteriaContext);
        consumer.accept(this.criteria, constructor);
        return this.dynamicValuesEnd(constructor.endConstructor());
    }


    @Override
    public final _RightParenClause<RR> leftParen(Object value) {
        this.createNewRow().add(CriteriaUtils.constantParam(this.criteriaContext, value));
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaDualSpec<RR> leftParen(Object value1, Object value2) {
        final List<_Expression> columnList;
        columnList = this.createNewRow();

        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value1));
        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value2));
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaQuadraSpec<RR> leftParen(Object value1, Object value2, Object value3, Object value4) {
        final List<_Expression> columnList;
        columnList = this.createNewRow();

        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value1));
        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value2));
        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value3));
        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value4));
        return this;
    }

    @Override
    public _RightParenClause<RR> leftParenLiteral(Object value) {
        this.createNewRow().add(CriteriaUtils.constantLiteral(this.criteriaContext, value));
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaDualSpec<RR> leftParenLiteral(Object value1, Object value2) {
        final List<_Expression> columnList;
        columnList = this.createNewRow();

        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value1));
        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value2));
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaQuadraSpec<RR> leftParenLiteral(Object value1, Object value2, Object value3, Object value4) {
        final List<_Expression> columnList;
        columnList = this.createNewRow();

        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value1));
        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value2));
        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value3));
        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value4));
        return this;
    }


    @Override
    public final _RightParenClause<RR> comma(Object value) {
        this.getCurrentRow().add(CriteriaUtils.constantParam(this.criteriaContext, value));
        return this;
    }

    @Override
    public Values._StaticValueRowCommaDualSpec<RR> comma(Object value1, Object value2) {
        final List<_Expression> columnList;
        columnList = this.getCurrentRow();

        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value1));
        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value2));
        return this;
    }


    @Override
    public final _RightParenClause<RR> comma(Object value1, Object value2, Object value3) {
        final List<_Expression> columnList;
        columnList = this.getCurrentRow();

        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value1));
        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value2));
        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value3));
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaQuadraSpec<RR> comma(Object value1, Object value2, Object value3, Object value4) {
        final List<_Expression> columnList;
        columnList = this.getCurrentRow();

        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value1));
        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value2));
        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value3));
        columnList.add(CriteriaUtils.constantParam(this.criteriaContext, value4));
        return this;
    }


    @Override
    public final _RightParenClause<RR> commaLiteral(Object value) {
        this.getCurrentRow().add(CriteriaUtils.constantLiteral(this.criteriaContext, value));
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaDualSpec<RR> commaLiteral(Object value1, Object value2) {
        final List<_Expression> columnList;
        columnList = this.getCurrentRow();

        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value1));
        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value2));
        return this;
    }


    @Override
    public final _RightParenClause<RR> commaLiteral(Object value1, Object value2, Object value3) {
        final List<_Expression> columnList;
        columnList = this.getCurrentRow();

        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value1));
        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value2));
        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value3));
        return this;
    }


    @Override
    public final Values._StaticValueRowCommaQuadraSpec<RR> commaLiteral(Object value1, Object value2, Object value3, Object value4) {
        final List<_Expression> columnList;
        columnList = this.getCurrentRow();

        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value1));
        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value2));
        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value3));
        columnList.add(CriteriaUtils.constantLiteral(this.criteriaContext, value4));
        return this;
    }


    @Override
    public final void appendSql(final _SqlContext context) {
        if (!(this instanceof SubValues)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        context.dialect().rowSet(this, context);
    }


    @Override
    public final V asValues() {
        if (this instanceof SubValues) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }
        return this.asQuery();
    }

    abstract VR dynamicValuesEnd(List<List<_Expression>> rowList);

    abstract List<_Expression> createNewRow();

    abstract List<_Expression> getCurrentRow();


    @Override
    final void onOrderBy() {
        //no-op
    }

    @Override
    final Void asUnionAndRowSet(UnionType unionType) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }

    @Override
    final void crossJoinEvent(boolean success) {
        throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
    }


}
