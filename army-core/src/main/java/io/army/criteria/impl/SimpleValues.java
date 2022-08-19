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
        super(criteriaContext, JoinableClause.voidClauseCreator());

        if (this instanceof SubValues) {
            CriteriaContextStack.push(this.context);
        } else {
            CriteriaContextStack.setContextStack(this.context);
        }

    }


    @Override
    public final VR values(Consumer<RowConstructor> consumer) {
        final CriteriaSupports.RowConstructorImpl constructor;
        constructor = new CriteriaSupports.RowConstructorImpl(this.context);
        consumer.accept(constructor);
        return this.dynamicValuesEnd(constructor.endConstructor());
    }

    @Override
    public final VR values(BiConsumer<C, RowConstructor> consumer) {
        final CriteriaSupports.RowConstructorImpl constructor;
        constructor = new CriteriaSupports.RowConstructorImpl(this.context);
        consumer.accept(this.criteria, constructor);
        return this.dynamicValuesEnd(constructor.endConstructor());
    }


    @Override
    public final _RightParenClause<RR> leftParen(Object value) {
        this.createNewRow().add(CriteriaUtils.constantLiteral(this.context, value));
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaDualSpec<RR> leftParen(Object value1, Object value2) {
        final List<_Expression> columnList;
        columnList = this.createNewRow();

        columnList.add(CriteriaUtils.constantLiteral(this.context, value1));
        columnList.add(CriteriaUtils.constantLiteral(this.context, value2));
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaQuadraSpec<RR> leftParen(Object value1, Object value2, Object value3, Object value4) {
        final List<_Expression> columnList;
        columnList = this.createNewRow();

        columnList.add(CriteriaUtils.constantLiteral(this.context, value1));
        columnList.add(CriteriaUtils.constantLiteral(this.context, value2));
        columnList.add(CriteriaUtils.constantLiteral(this.context, value3));
        columnList.add(CriteriaUtils.constantLiteral(this.context, value4));
        return this;
    }


    @Override
    public final _RightParenClause<RR> comma(Object value) {
        this.getCurrentRow().add(CriteriaUtils.constantLiteral(this.context, value));
        return this;
    }

    @Override
    public Values._StaticValueRowCommaDualSpec<RR> comma(Object value1, Object value2) {
        final List<_Expression> columnList;
        columnList = this.getCurrentRow();

        columnList.add(CriteriaUtils.constantLiteral(this.context, value1));
        columnList.add(CriteriaUtils.constantLiteral(this.context, value2));
        return this;
    }


    @Override
    public final _RightParenClause<RR> comma(Object value1, Object value2, Object value3) {
        final List<_Expression> columnList;
        columnList = this.getCurrentRow();

        columnList.add(CriteriaUtils.constantLiteral(this.context, value1));
        columnList.add(CriteriaUtils.constantLiteral(this.context, value2));
        columnList.add(CriteriaUtils.constantLiteral(this.context, value3));
        return this;
    }

    @Override
    public final Values._StaticValueRowCommaQuadraSpec<RR> comma(Object value1, Object value2, Object value3, Object value4) {
        final List<_Expression> columnList;
        columnList = this.getCurrentRow();

        columnList.add(CriteriaUtils.constantLiteral(this.context, value1));
        columnList.add(CriteriaUtils.constantLiteral(this.context, value2));
        columnList.add(CriteriaUtils.constantLiteral(this.context, value3));
        columnList.add(CriteriaUtils.constantLiteral(this.context, value4));
        return this;
    }


    @Override
    public final void appendSql(final _SqlContext context) {
        if (!(this instanceof SubValues)) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        context.parser().rowSet(this, context);
    }


    @Override
    public final V asValues() {
        return this.asQuery();
    }


    @Override
    final V internalAsRowSet(final boolean fromAsQueryMethod) {
        if (!fromAsQueryMethod) {
            throw CriteriaContextStack.castCriteriaApi(this.context);//VALUES statement don't support
        }
        if (this instanceof SubValues) {
            CriteriaContextStack.pop(this.context);
        } else {
            CriteriaContextStack.clearContextStack(this.context);
        }
        return this.onAsValues();
    }


    abstract V onAsValues();

    abstract VR dynamicValuesEnd(List<List<_Expression>> rowList);

    abstract List<_Expression> createNewRow();

    abstract List<_Expression> getCurrentRow();



    @Override
    final Void asUnionAndRowSet(UnionType unionType) {
        throw CriteriaContextStack.castCriteriaApi(this.context);
    }

    @Override
    final void crossJoinEvent(boolean success) {
        throw CriteriaContextStack.castCriteriaApi(this.context);
    }


}
