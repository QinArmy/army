package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.postgre._PostgreCteStatement;
import io.army.criteria.postgre.*;
import io.army.lang.Nullable;
import io.army.util._StringUtils;

import java.util.Collections;
import java.util.List;


abstract class PostgreSupports extends CriteriaSupports {


    private PostgreSupports() {
    }


    static final List<Selection> RETURNING_ALL = Collections.emptyList();

    static PostgreCteBuilder cteBuilder(final boolean recursive, final CriteriaContext context) {
        return new PostgreCteBuilderImpl(recursive, context);
    }


    enum MaterializedOption implements SQLWords {

        MATERIALIZED(" MATERIALIZED"),
        NOT_MATERIALIZED(" NOT MATERIALIZED");

        private final String spaceWord;

        MaterializedOption(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(MaterializedOption.class.getSimpleName())
                    .append(this.name())
                    .toString();
        }


    }//MaterializedOption


    static final class PostgreSubStatement implements _PostgreCteStatement {

        private final MaterializedOption option;

        private final SubStatement statement;


        PostgreSubStatement(@Nullable MaterializedOption option, SubStatement statement) {
            this.option = option;
            this.statement = statement;
        }

        @Override
        public void prepared() {
            this.statement.prepared();
        }

        @Override
        public boolean isPrepared() {
            return this.statement.isPrepared();
        }

        @Override
        public SQLWords materializedOption() {
            return this.option;
        }

        @Override
        public SubStatement subStatement() {
            return this.statement;
        }


    }//PostgreSubStatement


    private static abstract class PostgreDynamicDmlCteLeftParenClause<I extends Item>
            extends ParenStringConsumerClause<Statement._StaticAsClaus<I>>
            implements DialectStatement._SimpleCteLeftParenSpec<I>
            , Statement._StaticAsClaus<I>
            , Statement._AsCteClause<PostgreCteBuilder> {

        private final String name;

        final PostgreCteBuilderImpl cteBuilder;

        private List<String> columnAliasList;

        private PostgreDynamicDmlCteLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(cteBuilder.context);
            this.name = name;
            this.cteBuilder = cteBuilder;
        }


        @Override
        public final PostgreCteBuilder asCte() {
            return this.cteBuilder;
        }

        @Override
        final Statement._StaticAsClaus<I> stringConsumerEnd(final List<String> stringList) {
            if (this.columnAliasList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.columnAliasList = stringList;
            return this;
        }

        final Statement._AsCteClause<PostgreCteBuilder> subStmtEnd(final SubStatement stmt) {
            CriteriaUtils.createAndAddCte(this.cteBuilder.context, this.name, this.columnAliasList, stmt);
            return this;
        }

    }//PostgreDynamicDmlCteLeftParenClause


    private static final class PostgreDynamicInsertLeftParenClause
            extends PostgreDynamicDmlCteLeftParenClause<
            PostgreInsert._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>>>
            implements PostgreInsert._DynamicSubInsertSpec {

        private PostgreDynamicInsertLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }

        @Override
        public PostgreInsert._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>> as() {
            return PostgreInserts.dynamicSubInsert(this.context, this::subStmtEnd);
        }

    }//PostgreDynamicInsertLeftParenClause

    private static final class PostgreDynamicUpdateLeftParenClause
            extends PostgreDynamicDmlCteLeftParenClause<
            PostgreUpdate._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>>>
            implements PostgreUpdate._DynamicCteUpdateSpec {

        private PostgreDynamicUpdateLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }


        @Override
        public PostgreUpdate._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>> as() {
            return PostgreUpdates.dynamicCteUpdate(this.cteBuilder.context, this::subStmtEnd);
        }

    }//PostgreDynamicUpdateLeftParenClause

    private static final class PostgreDynamicDeleteLeftParenClause
            extends PostgreDynamicDmlCteLeftParenClause<
            PostgreDelete._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>>>
            implements PostgreDelete._DynamicCteDeleteSpec {

        private PostgreDynamicDeleteLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }


        @Override
        public PostgreDelete._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>> as() {
            return PostgreDeletes.dynamicCteDelete(this.cteBuilder.context, this::subStmtEnd);
        }

    }//PostgreDynamicDeleteLeftParenClause


    private static final class PostgreDynamicQueryLeftParenClause
            extends PostgreDynamicDmlCteLeftParenClause<
            PostgreQuery._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>>>
            implements PostgreQuery._DynamicCteQuerySpec {

        private PostgreDynamicQueryLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }

        @Override
        public PostgreQuery._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>> as() {
            return PostgreQueries.dynamicCteQuery(this.cteBuilder.context, this::subStmtEnd);
        }


    }//PostgreDynamicQueryLeftParenClause


    private static final class PostgreDynamicValuesLeftParenClause
            extends PostgreDynamicDmlCteLeftParenClause<
            PostgreValues._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>>>
            implements PostgreValues._DynamicCteValuesSpec {

        private PostgreDynamicValuesLeftParenClause(String name, PostgreCteBuilderImpl cteBuilder) {
            super(name, cteBuilder);
        }

        @Override
        public PostgreValues._DynamicSubMaterializedSpec<Statement._AsCteClause<PostgreCteBuilder>> as() {
            return PostgreValuesStatements.dynamicCteValues(this.cteBuilder.context, this::subStmtEnd);
        }

    }//PostgreDynamicValuesLeftParenClause


    private static final class PostgreCteBuilderImpl implements PostgreCteBuilder {

        private final boolean recursive;

        private final CriteriaContext context;

        private PostgreCteBuilderImpl(final boolean recursive, CriteriaContext context) {
            this.recursive = recursive;
            this.context = context;
            context.onBeforeWithClause(recursive);
        }


        @Override
        public PostgreInsert._DynamicSubInsertSpec singleInsert(String name) {
            this.context.onStartCte(name);
            return new PostgreDynamicInsertLeftParenClause(name, this);
        }

        @Override
        public PostgreUpdate._DynamicCteUpdateSpec singleUpdate(String name) {
            this.context.onStartCte(name);
            return new PostgreDynamicUpdateLeftParenClause(name, this);
        }

        @Override
        public PostgreDelete._DynamicCteDeleteSpec singleDelete(String name) {
            this.context.onStartCte(name);
            return new PostgreDynamicDeleteLeftParenClause(name, this);
        }

        @Override
        public PostgreQuery._DynamicCteQuerySpec query(String name) {
            this.context.onStartCte(name);
            return new PostgreDynamicQueryLeftParenClause(name, this);
        }

        @Override
        public PostgreValues._DynamicCteValuesSpec cteValues(String name) {
            this.context.onStartCte(name);
            return new PostgreDynamicValuesLeftParenClause(name, this);
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }


    }//PostgreCteBuilderImpl


}
