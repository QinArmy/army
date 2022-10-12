package io.army.criteria;

import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

public interface StandardUpdate extends StandardStatement {


    interface _StandardWhereAndSpec extends Update._UpdateWhereAndClause<_StandardWhereAndSpec>, Update._UpdateSpec {

    }

    interface _StandardWhereSpec<F extends TableField> extends _StandardSetClause<F>
            , _WhereClause<Update._UpdateSpec, _StandardWhereAndSpec> {


    }

    interface _StandardSetClause<F extends TableField> extends Update._SimpleSetClause<F, _StandardWhereSpec<F>> {

    }

    interface _StandardSingleUpdateClause {

        <T> _StandardSetClause<FieldMeta<T>> update(SingleTableMeta<T> table, String tableAlias);

        <P> _StandardSetClause<FieldMeta<P>> update(ComplexTableMeta<P, ?> table, String tableAlias);

    }

    interface _StandardDomainUpdateClause {

        <T> _StandardSetClause<FieldMeta<? super T>> update(TableMeta<T> table, String tableAlias);

    }

    interface _StandardBatchWhereAndSpec extends Update._UpdateWhereAndClause<_StandardBatchWhereAndSpec>
            , _BatchParamClause<Update._UpdateSpec> {

    }

    interface _StandardBatchWhereSpec<F extends TableField> extends _StandardBatchSetClause<F>
            , _WhereClause<_BatchParamClause<Update._UpdateSpec>, _StandardBatchWhereAndSpec>
            , _BatchParamClause<Update._UpdateSpec> {

    }

    interface _StandardBatchSetClause<F extends TableField>
            extends Update._BatchSetClause<F, _StandardBatchWhereSpec<F>> {


    }

    interface _StandardBatchSingleUpdateClause {

        <T> _StandardBatchSetClause<FieldMeta<T>> update(SingleTableMeta<T> table, String tableAlias);

        <P> _StandardBatchSetClause<FieldMeta<P>> update(ComplexTableMeta<P, ?> table, String tableAlias);

    }

    interface _StandardBatchDomainUpdateClause {

        <T> _StandardBatchSetClause<FieldMeta<? super T>> update(TableMeta<T> table, String tableAlias);

    }
}
