/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._FunctionField;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableNameElement;
import io.army.criteria.postgre.PostgreQuery;
import io.army.criteria.postgre.PostgreWindow;
import io.army.criteria.standard.StandardQuery;
import io.army.dialect.*;
import io.army.dialect.postgre.PostgreDialect;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingType;
import io.army.mapping.TextType;
import io.army.mapping.XmlType;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;
import io.army.stmt.SimpleStmt;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;

abstract class PostgreFunctionUtils extends DialectFunctionUtils {

    private PostgreFunctionUtils() {
    }


    static XmlNamedElementPart<XmlAttributes> xmlAttributes() {
        return new XmlNamedElementPart<>(true, XmlAttributes::new);
    }

    static XmlNamedElementPart<PostgreDocumentFunctions.XmlNameSpaces> xmlNamespaces() {
        return new XmlNamedElementPart<>(false, XmlNameSpaces::new);
    }

    static XmlNamedElementPart<SimpleExpression> xmlForest() {
        return new XmlNamedElementPart<>(true, PostgreFunctionUtils::onXmlForestEnd);
    }

    static XmlTableColumnsClause xmlTableColumnsClause() {
        return new XmlTableColumnsClause();
    }

    static Expression tableNameExp(final @Nullable TableMeta<?> table) {
        if (table == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return new TableNameExpression(table);
    }


    static Expression queryStringExp(final @Nullable Select query, final @Nullable Visible visible) {
        if (query == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (visible == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!(query instanceof PostgreQuery
                || query instanceof StandardQuery
                || query instanceof SimpleQueries.UnionSelect)) {
            String m = String.format("%s don't support %s", Database.PostgreSQL, query.getClass().getName());
            throw ContextStack.clearStackAndCriteriaError(m);
        }
        return new QueryExpression(query, visible);
    }


    static PostgreWindowFunctions._OverSpec zeroArgWindowFunc(String name, TypeMeta returnType) {
        return new ZeroArgWindowFunc(name, returnType);
    }

    static PostgreWindowFunctions._OverSpec oneArgWindowFunc(String name, Expression one, TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        return new OneArgWindowFunc(name, one, returnType);
    }

    static PostgreWindowFunctions._OverSpec twoArgWindowFunc(final String name, final Expression one, final Expression two,
                                                             final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return new TwoArgWindowFunc(name, one, two, returnType);
    }

    static PostgreWindowFunctions._OverSpec threeArgWindowFunc(final String name, final Expression one, final Expression two,
                                                               final Expression three, final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        return new ThreeArgWindowFunc(name, one, two, three, returnType);
    }

    static PostgreWindowFunctions._AggWindowFunc oneArgAggWindowFunc(final String name, final Expression one,
                                                                     final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        return new OneArgAggWindowFunc(name, one, returnType);
    }

    static PostgreWindowFunctions._AggWindowFunc twoArgAggWindowFunc(final String name, final Expression one,
                                                                     final Expression two, final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return new TwoArgAggWindowFunc(name, one, two, returnType);
    }

    static PostgreWindowFunctions._PgAggFunc oneArgAggFunc(final String name, final @Nullable SQLs.ArgDistinct modifier,
                                                           final Expression one,
                                                           final @Nullable Consumer<Statement._SimpleOrderByClause> consumer,
                                                           final TypeMeta returnType) {

        return _oneArgAggFunc(name, true, modifier, one, consumer, returnType);
    }

    static PostgreWindowFunctions._PgAggFunc twoArgAggFunc(final String name, final @Nullable SQLs.ArgDistinct modifier,
                                                           final Expression one, final Expression two,
                                                           final @Nullable Consumer<Statement._SimpleOrderByClause> consumer,
                                                           final TypeMeta returnType) {
        return _twoArgAggFunc(name, true, modifier, one, two, consumer, returnType);
    }

    static PostgreWindowFunctions._PgAggFunc oneUserArgAggFunc(final String name, final @Nullable SQLs.ArgDistinct modifier,
                                                               final Expression one,
                                                               final @Nullable Consumer<Statement._SimpleOrderByClause> consumer,
                                                               final TypeMeta returnType) {

        return _oneArgAggFunc(name, false, modifier, one, consumer, returnType);
    }

    static PostgreWindowFunctions._PgAggFunc twoUserArgAggFunc(final String name, final @Nullable SQLs.ArgDistinct modifier,
                                                               final Expression one, final Expression two,
                                                               final @Nullable Consumer<Statement._SimpleOrderByClause> consumer,
                                                               final TypeMeta returnType) {
        return _twoArgAggFunc(name, false, modifier, one, two, consumer, returnType);
    }

    static PostgreWindowFunctions._AggWithGroupClause zeroArgWithGroupAggFunc(final String name,
                                                                              final TypeMeta returnType) {
        return new ZeroArgWithGroupAggFunc(name, true, returnType);
    }

    /**
     * user-defined WITH GROUP aggregate function
     */
    static PostgreWindowFunctions._AggWithGroupClause zeroArgMyWithGroupAggFunc(final String name,
                                                                                final TypeMeta returnType) {
        return new ZeroArgWithGroupAggFunc(name, false, returnType);
    }

    static PostgreWindowFunctions._AggWithGroupClause oneArgWithGroupAggFunc(final String name,
                                                                             final Expression one,
                                                                             final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        return new OneArgWithGroupAggFunc(name, true, one, returnType);
    }

    /**
     * user-defined WITH GROUP aggregate function
     */
    static PostgreWindowFunctions._AggWithGroupClause oneArgMyWithGroupAggFunc(final String name,
                                                                               final Expression one,
                                                                               final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        return new OneArgWithGroupAggFunc(name, false, one, returnType);
    }

    /**
     * user-defined WITH GROUP aggregate function
     */
    static PostgreWindowFunctions._AggWithGroupClause multiArgMyWithGroupAggFunc(final String name,
                                                                                 final List<ArmyExpression> argList,
                                                                                 final TypeMeta returnType) {
        for (ArmyExpression exp : argList) {
            if (!(exp instanceof FunctionArg.SingleFunctionArg)) {
                throw CriteriaUtils.funcArgError(name, exp);
            }
        }
        return new MultiArgWithGroupAggFunc(name, false, argList, returnType);
    }

    static TypeMeta unaryOrderSetType(UnaryOperator<MappingType> function) {
        return new UnaryOrderedSetType(function);
    }

    static TypeMeta biOrderedSetType(Expression exp, BinaryOperator<MappingType> function) {
        return new BiOrderedSetType(exp, function);
    }

    static Functions._TabularWithOrdinalityFunction rowsFrom(Consumer<Postgres._RowsFromSpaceClause> consumer) {
        final PostgreRowsFromFunction func;
        func = new PostgreRowsFromFunction();
        consumer.accept(func);
        return func.endFunc();
    }

    static Functions._TabularWithOrdinalityFunction rowsFrom(SQLs.SymbolSpace space, Consumer<Postgres.RowFromConsumer> consumer) {
        if (space != SQLs.SPACE) {
            throw CriteriaUtils.errorSymbol(space);
        }
        final PostgreRowsFromFunction func;
        func = new PostgreRowsFromFunction();
        consumer.accept(func);
        return func.endFunc();
    }

    /**
     * @see #oneArgAggFunc(String, SQLs.ArgDistinct, Expression, Consumer, TypeMeta)
     */
    private static PostgreWindowFunctions._PgAggFunc _oneArgAggFunc(final String name, final boolean buildIn,
                                                                    final @Nullable SQLs.ArgDistinct modifier,
                                                                    final Expression one,
                                                                    final @Nullable Consumer<Statement._SimpleOrderByClause> consumer,
                                                                    final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        return new OneArgAggFunc(name, buildIn, modifier, one, consumeOrderBy(consumer), returnType);
    }

    /**
     * @see #twoArgAggFunc(String, SQLs.ArgDistinct, Expression, Expression, Consumer, TypeMeta)
     */
    private static PostgreWindowFunctions._PgAggFunc _twoArgAggFunc(final String name, final boolean buildIn,
                                                                    final @Nullable SQLs.ArgDistinct modifier,
                                                                    final Expression one, final Expression two,
                                                                    final @Nullable Consumer<Statement._SimpleOrderByClause> consumer,
                                                                    final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return new TwoArgAggFunc(name, buildIn, modifier, one, two, consumeOrderBy(consumer), returnType);
    }


    private static SimpleExpression onXmlForestEnd(ArmyFuncClause clause) {
        assert clause instanceof XmlNamedElementPart;
        return FunctionUtils.clauseFunc("XMLFOREST", clause, XmlType.TEXT);
    }

    private static CriteriaException noWithGroupClause() {
        return ContextStack.clearStackAndCriteriaError("error,you don't invoke WITHIN GROUP clause");
    }

    @Nullable
    private static OrderByOptionClause consumeOrderBy(final @Nullable Consumer<Statement._SimpleOrderByClause> consumer) {
        if (consumer == null) {
            return null;
        }
        OrderByOptionClause clause;
        clause = FunctionUtils.orderByOptionClause();
        consumer.accept(clause);
        if (clause.endOrderByClauseIfNeed().size() == 0) {
            clause = null;
        }
        return clause;
    }


    /*-------------------below inner class  -------------------*/


    static final class TableNameExpression extends NonOperationExpression
            implements FunctionArg.SingleFunctionArg, _TableNameElement {

        private final TableMeta<?> table;

        private TableNameExpression(TableMeta<?> table) {
            this.table = table;
        }

        @Override
        public TableMeta<?> tableMeta() {
            return this.table;
        }

        @Override
        public TypeMeta typeMeta() {
            return TextType.INSTANCE;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            context.appendLiteral(this.typeMeta(), context.parser().sqlElement(this));
        }


    }//TableNameExpression


    static final class XmlNamedElementPart<R extends Item> implements ArmyFuncClause,
            Postgres._XmlNamedElementFieldClause {

        private final boolean supportField;

        private final Function<ArmyFuncClause, R> function;

        private final CriteriaContext outerContext;

        private List<Object> attValueList;

        private XmlNamedElementPart(boolean supportField, Function<ArmyFuncClause, R> function) {
            this.supportField = supportField;
            this.function = function;
            this.outerContext = ContextStack.peek();
        }


        @SuppressWarnings("unchecked")
        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            final List<Object> attValueList = this.attValueList;
            final int attValueSize;
            if (attValueList == null
                    || attValueList instanceof ArrayList
                    || (attValueSize = attValueList.size()) == 0) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

            final DialectParser parser;
            parser = context.parser();


            Object attObject;
            _Pair<ArmyExpression, String> attrPair;
            for (int i = 0; i < attValueSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                attObject = attValueList.get(i);
                if (attObject instanceof _Pair) {
                    attrPair = (_Pair<ArmyExpression, String>) attObject;

                    attrPair.first.appendSql(sqlBuilder, context);
                    sqlBuilder.append(SQLs.AS.spaceRender());
                    sqlBuilder.append(_Constant.SPACE);
                    parser.identifier(attrPair.second, sqlBuilder);

                } else if (attObject instanceof SqlField) {
                    ((ArmyExpression) attObject).appendSql(sqlBuilder, context);
                } else {
                    //no bug,never here
                    throw new IllegalStateException();
                }
            }

        }


        @Override
        public Postgres._XmlNamedElementFieldClause accept(final @Nullable SqlField field) {
            if (!this.supportField) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            List<Object> attValueList = this.attValueList;
            if (attValueList == null) {
                attValueList = _Collections.arrayList();
                this.attValueList = attValueList;
            } else if (!(attValueList instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            if (field == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (!(field instanceof OperationDataField)) {
                throw ContextStack.nonArmyExp(this.outerContext);
            }
            attValueList.add(field);
            return this;
        }

        @Override
        public Postgres._XmlNamedElementFieldClause accept(
                final @Nullable Expression attValue, SQLs.WordAs as, final @Nullable String attName) {

            List<Object> attValueList = this.attValueList;
            if (attValueList == null) {
                attValueList = _Collections.arrayList();
                this.attValueList = attValueList;
            } else if (!(attValueList instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            if (attValue == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (!(attValue instanceof OperationExpression)) {
                throw ContextStack.criteriaError(this.outerContext, "expression isn't operable expression.");
            } else if (attName == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (!_DialectUtils.isSimpleIdentifier(attName)) {
                String m = String.format("attName[%s] must be a simple identifier.", attName);
                throw ContextStack.criteriaError(this.outerContext, m);
            }
            attValueList.add(_Pair.create((ArmyExpression) attValue, attName));
            return this;
        }

        @Override
        public Postgres._XmlNamedElementFieldClause accept(BiFunction<MappingType, String, Expression> funcRef, String attValue, SQLs.WordAs as, String attName) {
            return this.accept(funcRef.apply(TextType.INSTANCE, attValue), as, attName);
        }

        R endNamedPart() {
            final List<Object> attValueList = this.attValueList;
            if (attValueList == null || attValueList.size() == 0) {
                throw ContextStack.criteriaError(this.outerContext, "You don't add any thing.");
            } else if (attValueList instanceof ArrayList) {
                this.attValueList = _Collections.unmodifiableList(attValueList);
            } else {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return this.function.apply(this);
        }


    }//XmlAttributesFunc

    private static abstract class XmlNamedElementPartConsumer implements ArmyFuncClause {

        private final String name;

        private final ArmyFuncClause clause;

        private XmlNamedElementPartConsumer(String name, ArmyFuncClause clause) {
            assert clause instanceof XmlNamedElementPart;
            this.name = name;
            this.clause = clause;
        }

        @Override
        public final void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

            sqlBuilder.append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);
            this.clause.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


    }//XmlNamedElementPartConsumer

    static final class XmlAttributes extends XmlNamedElementPartConsumer
            implements PostgreStringFunctions.XmlAttributes {

        private XmlAttributes(ArmyFuncClause clause) {
            super("XMLATTRIBUTES", clause);
        }


    }//XmlAttributes

    static final class XmlNameSpaces extends XmlNamedElementPartConsumer
            implements PostgreDocumentFunctions.XmlNameSpaces {

        private XmlNameSpaces(ArmyFuncClause clause) {
            super("XMLNAMESPACES", clause);
        }


    }//XmlNameSpaces


    private static CriteriaException xmlTableObjectArrayError(@Nullable CriteriaContext context, MappingType type) {
        String m = String.format("%s javaType() return %s,unsupported by %s",
                type, Object.class.getName(), XmlTableColumnsClause.XMLTABLE);
        final CriteriaException e;
        if (context == null) {
            e = ContextStack.clearStackAndCriteriaError(m);
        } else {
            e = ContextStack.clearStackAndCriteriaError(m);
        }
        return e;
    }


    static final class XmlTableColumnsClause implements Postgres._XmlTableColumnsClause,
            Postgres.XmlTableCommaClause,
            ArmyFuncClause {

        static final String XMLTABLE = "XMLTABLE";

        private final CriteriaContext outerContext;

        private List<XmlTableColumn> columnList = _Collections.arrayList();

        private Map<String, Selection> selectionMap = _Collections.hashMap();

        private XmlTableColumnsClause() {
            this.outerContext = ContextStack.peek();
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            final List<XmlTableColumn> columnList = this.columnList;
            final int columnSize;
            if (columnList == null || columnList instanceof ArrayList || (columnSize = columnList.size()) == 0) {
                throw _Exceptions.castCriteriaApi();
            }


            for (int i = 0; i < columnSize; i++) {
                if (i == 0) {
                    sqlBuilder.append(" COLUMNS");
                } else {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                columnList.get(i).appendSql(sqlBuilder, context);
            }

        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, SQLs.WordPath path,
                                                    Expression columnExp, SQLs.WordDefault wordDefault,
                                                    Expression defaultExp,
                                                    SQLs.NullOption nullOption) {
            return this.comma(name, type, path, columnExp, wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, SQLs.WordDefault wordDefault,
                                                    Expression defaultExp, SQLs.NullOption nullOption) {
            return this.comma(name, type, wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, SQLs.WordPath path,
                                                    Expression columnExp, SQLs.NullOption nullOption) {
            return this.comma(name, type, path, columnExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, SQLs.WordPath path,
                                                    Expression columnExp, SQLs.WordDefault wordDefault,
                                                    Expression defaultExp) {
            return this.comma(name, type, path, columnExp, wordDefault, defaultExp);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, SQLs.NullOption nullOption) {
            return this.comma(name, type, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, SQLs.WordDefault wordDefault,
                                                    Expression defaultExp) {
            return this.comma(name, type, wordDefault, defaultExp);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, SQLs.WordPath path,
                                                    Expression columnExp) {
            return this.comma(name, type, path, columnExp);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type) {
            return this.comma(name, type);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, SQLs.WordsForOrdinality forOrdinality) {
            return this.comma(name, forOrdinality);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, SQLs.WordPath path,
                                                    BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                    String columnExp, SQLs.WordDefault wordDefault,
                                                    Expression defaultExp, SQLs.NullOption nullOption) {
            return this.comma(name, type, path, funcRefForColumnExp, columnExp, wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, SQLs.WordPath path,
                                                    BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                    String columnExp, SQLs.NullOption nullOption) {
            return this.comma(name, type, path, funcRefForColumnExp, columnExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, SQLs.WordPath path,
                                                    BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                    String columnExp, SQLs.WordDefault wordDefault,
                                                    Expression defaultExp) {
            return this.comma(name, type, path, funcRefForColumnExp, columnExp, wordDefault, defaultExp);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, SQLs.WordPath path,
                                                  @Nullable Expression columnExp, SQLs.WordDefault wordDefault,
                                                  @Nullable Expression defaultExp,
                                                  @Nullable SQLs.NullOption nullOption) {
            if (columnExp == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (defaultExp == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (nullOption == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, path, columnExp, wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, SQLs.WordDefault wordDefault,
                                                  @Nullable Expression defaultExp,
                                                  @Nullable SQLs.NullOption nullOption) {
            if (defaultExp == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (nullOption == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, SQLs.PATH, null, wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, SQLs.WordPath path,
                                                  @Nullable Expression columnExp,
                                                  @Nullable SQLs.NullOption nullOption) {
            if (columnExp == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (nullOption == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, path, columnExp, SQLs.DEFAULT, null, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, SQLs.WordPath path,
                                                  @Nullable Expression columnExp, SQLs.WordDefault wordDefault,
                                                  @Nullable Expression defaultExp) {
            if (columnExp == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (defaultExp == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, path, columnExp, wordDefault, defaultExp, null);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type,
                                                  @Nullable SQLs.NullOption nullOption) {
            if (nullOption == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, SQLs.PATH, null, SQLs.DEFAULT, null, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, SQLs.WordDefault wordDefault,
                                                  @Nullable Expression defaultExp) {
            if (defaultExp == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, SQLs.PATH, null, SQLs.DEFAULT, defaultExp, null);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, SQLs.WordPath path,
                                                  @Nullable Expression columnExp) {
            if (columnExp == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, SQLs.PATH, columnExp, SQLs.DEFAULT, null, null);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type) {
            return this.onAdd(name, type, SQLs.PATH, null, SQLs.DEFAULT, null, null);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(@Nullable String name, SQLs.WordsForOrdinality forOrdinality) {
            if (name == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (!_DialectUtils.isSimpleIdentifier(name)) {
                throw CriteriaUtils.funcColumnNameIsNotSimpleIdentifier(this.outerContext, XMLTABLE, name);
            } else if (forOrdinality != SQLs.FOR_ORDINALITY) {
                throw CriteriaUtils.funcArgError(XMLTABLE, forOrdinality);
            }
            return this.onAddColumn(new XmlTableOrdinalityColumn(name));
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, SQLs.WordPath path,
                                                  BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                  String columnExp, SQLs.WordDefault wordDefault,
                                                  Expression defaultExp, SQLs.NullOption nullOption) {
            return this.comma(name, type, path, funcRefForColumnExp.apply(TextType.INSTANCE, columnExp),
                    wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, SQLs.WordPath path,
                                                  BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                  String columnExp, SQLs.NullOption nullOption) {
            return this.comma(name, type, path, funcRefForColumnExp.apply(TextType.INSTANCE, columnExp), nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, SQLs.WordPath path,
                                                  BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                  String columnExp, SQLs.WordDefault wordDefault,
                                                  Expression defaultExp) {
            return this.comma(name, type, path, funcRefForColumnExp.apply(TextType.INSTANCE, columnExp),
                    wordDefault, defaultExp);
        }

        /**
         * @return a unmodified list
         */
        List<XmlTableColumn> endColumnsClause() {
            List<XmlTableColumn> columnList = this.columnList;
            final Map<String, Selection> selectionMap = this.selectionMap;
            if (!(columnList instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            } else if (columnList.size() == 0) {
                throw ContextStack.criteriaError(this.outerContext, "You don't add any column.");
            }
            columnList = _Collections.unmodifiableList(columnList);
            this.columnList = columnList;
            this.selectionMap = _Collections.unmodifiableMap(selectionMap);
            return columnList;
        }

        /**
         * @return a unmodified map
         */
        Map<String, Selection> getSelectionMap() {
            final Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null || selectionMap instanceof HashMap) {
                // no bug,never here
                throw new IllegalStateException();
            }
            return selectionMap;
        }


        private Postgres.XmlTableCommaClause onAdd(
                final @Nullable String name, @Nullable final MappingType type, final SQLs.WordPath path,
                final @Nullable Expression columnExp, final SQLs.WordDefault wordDefault,
                final @Nullable Expression defaultExp, final @Nullable SQLs.NullOption nullOption) {

            if (name == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (!_DialectUtils.isSimpleIdentifier(name)) {
                throw CriteriaUtils.funcColumnNameIsNotSimpleIdentifier(this.outerContext, XMLTABLE, name);
            } else if (type == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (path != SQLs.PATH) {
                throw CriteriaUtils.funcArgError(XMLTABLE, path);
            } else if (!(columnExp == null || columnExp instanceof OperationExpression)) {
                throw CriteriaUtils.funcArgError(XMLTABLE, columnExp);
            } else if (wordDefault != SQLs.DEFAULT) {
                throw CriteriaUtils.funcArgError(XMLTABLE, wordDefault);
            } else if (!(defaultExp == null || defaultExp instanceof OperationExpression)) {
                throw CriteriaUtils.funcArgError(XMLTABLE, defaultExp);
            } else if (!(nullOption == null || nullOption == SQLs.NULL || nullOption == SQLs.NOT_NULL)) {
                throw CriteriaUtils.funcArgError(XMLTABLE, nullOption);
            }
            if (type instanceof MappingType.SqlArrayType && type.javaType() == Object.class) {
                throw xmlTableObjectArrayError(this.outerContext, type);
            }
            return this.onAddColumn(new XmlTableDataColumn(name, type, columnExp, defaultExp, nullOption));
        }


        private Postgres.XmlTableCommaClause onAddColumn(final XmlTableColumn column) {
            final List<XmlTableColumn> columnList = this.columnList;
            final Map<String, Selection> selectionMap = this.selectionMap;
            if (!(columnList instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            if (selectionMap.putIfAbsent(column.name, column) != null) {
                throw CriteriaUtils.funcColumnDuplicate(this.outerContext, XMLTABLE, column.name);
            }
            columnList.add(column);
            return this;
        }


    }//XmlTableColumnsClause


    private static abstract class XmlTableColumn extends OperationDataField {

        final String name;

        final MappingType type;


        private XmlTableColumn(String name, MappingType type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public final void appendSelectItem(final StringBuilder sqlBuilder, final _SqlContext context) {
            // no bug, never here
            throw new UnsupportedOperationException("invoking error");
        }

        @Override
        public final String fieldName() {
            return this.name;
        }

        @Override
        public final String label() {
            return this.name;
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.type;
        }

        @Override
        public final TableField tableField() {
            //always null
            return null;
        }

        @Override
        public final Expression underlyingExp() {
            //always null
            return null;
        }


    }//XmlTableColumn

    private static final class XmlTableDataColumn extends XmlTableColumn {


        private final ArmyExpression columnExp;

        private final ArmyExpression defaultExp;

        private final SQLs.NullOption nullOption;


        private XmlTableDataColumn(String name, MappingType type, @Nullable Expression columnExp,
                                   @Nullable Expression defaultExp,
                                   @Nullable SQLs.NullOption nullOption) {
            super(name, type);
            this.columnExp = (ArmyExpression) columnExp;
            this.defaultExp = (ArmyExpression) defaultExp;
            this.nullOption = nullOption;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            final DialectParser parser;
            parser = context.parser();

            sqlBuilder.append(_Constant.SPACE);

            parser.identifier(this.name, sqlBuilder)
                    .append(_Constant.SPACE);

            parser.typeName(this.type, sqlBuilder);

            final ArmyExpression columnExp = this.columnExp, defaultExp = this.defaultExp;
            if (columnExp != null) {
                sqlBuilder.append(SQLs.PATH.spaceRender());
                columnExp.appendSql(sqlBuilder, context);
            }

            if (defaultExp != null) {
                sqlBuilder.append(((SQLWords) SQLs.DEFAULT).spaceRender());
                defaultExp.appendSql(sqlBuilder, context);
            }

            final SQLs.NullOption nullOption = this.nullOption;
            if (nullOption != null) {
                sqlBuilder.append(((SQLWords) nullOption).spaceRender());
            }

        }


    }//XmlTableDataColumn

    private static final class XmlTableOrdinalityColumn extends XmlTableColumn {

        private XmlTableOrdinalityColumn(String name) {
            super(name, IntegerType.INSTANCE);
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

            sqlBuilder.append(_Constant.SPACE);

            context.identifier(this.name, sqlBuilder);
            sqlBuilder.append(SQLs.FOR_ORDINALITY.spaceRender());

        }


    }//XmlTableOrdinalityColumn

    private static final class QueryExpression extends NonOperationExpression
            implements FunctionArg.SingleFunctionArg {

        private final Select query;

        private final Visible visible;

        /**
         * @see #queryStringExp(Select, Visible)
         */
        private QueryExpression(Select query, Visible visible) {
            this.query = query;
            this.visible = visible;
        }

        @Override
        public TypeMeta typeMeta() {
            return TextType.INSTANCE;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            final SimpleStmt stmt;
            stmt = (SimpleStmt) context.parser().select(this.query, false, this.visible);
            if (stmt.paramGroup().size() > 0) {
                throw new CriteriaException("query expression couldn't have any parameter.");
            }
            context.appendLiteral(this.typeMeta(), stmt.sqlText());
        }


    }//QueryExpression


    private static abstract class PostgreWindowFunction
            extends WindowFunctions.WindowFunction<PostgreWindow._PartitionBySpec>
            implements PostgreWindowFunctions._OverSpec {

        private PostgreWindowFunction(String name, TypeMeta returnType) {
            super(name, returnType);
        }

        @Override
        final boolean isDontSupportWindow(Dialect dialect) {
            if (!(dialect instanceof PostgreDialect)) {
                throw dialectError(dialect);
            }
            return false;
        }

        @Override
        final PostgreWindow._PartitionBySpec createAnonymousWindow(@Nullable String existingWindowName) {
            return PostgreSupports.anonymousWindow(this.outerContext, existingWindowName);
        }


    }//PostgreWindowFunction


    private static final class ZeroArgWindowFunc extends PostgreWindowFunction implements NoArgFunction {

        private ZeroArgWindowFunc(String name, TypeMeta returnType) {
            super(name, returnType);
        }

        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            //no-op
        }

        @Override
        void argToString(StringBuilder builder) {
            //no-op
        }

    }//ZeroArgWindowFunc

    private static final class OneArgWindowFunc extends PostgreWindowFunction {

        private final ArmyExpression one;

        private OneArgWindowFunc(String name, Expression one, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, _SqlContext context) {
            this.one.appendSql(sqlBuilder, context);
        }

        @Override
        void argToString(StringBuilder builder) {
            builder.append(this.one);
        }

    }//OneArgWindowFunc

    private static final class TwoArgWindowFunc extends PostgreWindowFunction {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private TwoArgWindowFunc(String name, Expression one, Expression two, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, _SqlContext context) {
            this.one.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.COMMA);
            this.two.appendSql(sqlBuilder, context);
        }

        @Override
        void argToString(StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.COMMA)
                    .append(this.two);
        }

    }//TwoArgWindowFunc

    private static final class ThreeArgWindowFunc extends PostgreWindowFunction {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private final ArmyExpression three;

        private ThreeArgWindowFunc(String name, Expression one, Expression two, Expression three, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
            this.three = (ArmyExpression) three;
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, _SqlContext context) {
            this.one.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.COMMA);
            this.two.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.COMMA);
            this.three.appendSql(sqlBuilder, context);
        }

        @Override
        void argToString(StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.COMMA)
                    .append(this.two)
                    .append(_Constant.COMMA)
                    .append(this.three);
        }

    }//ThreeArgWindowFunc


    /*-------------------below Aggregate Functions-------------------*/

    private static final class AggFuncFilterClause implements PostgreWindowFunctions._PgAggFuncFilterClause<Item>, Item {

        private final CriteriaContext outerContext;

        private List<_Predicate> whereList;

        private AggFuncFilterClause(CriteriaContext outerContext) {
            this.outerContext = outerContext;
        }

        @Override
        public Item filter(Consumer<Statement._SimpleWhereClause> consumer) {
            this.ifFilter(consumer);
            final List<_Predicate> whereList = this.whereList;
            if (whereList == null || whereList.size() == 0) {
                throw CriteriaUtils.dontAddAnyItem();
            }
            return this;
        }

        @Override
        public Item ifFilter(Consumer<Statement._SimpleWhereClause> consumer) {
            final WhereClause.SimpleWhereClause whereClause;
            whereClause = new WhereClause.SimpleWhereClause(this.outerContext);
            consumer.accept(whereClause);
            if (this.whereList != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.whereList = whereClause.endWhereClauseIfNeed();
            return this;
        }

        private void appendOuterClause(final StringBuilder sqlBuilder, final _SqlContext context) {
            final List<_Predicate> whereList = this.whereList;
            final int predicateSize;
            if (whereList == null || (predicateSize = whereList.size()) == 0) {
                return;
            }
            sqlBuilder.append(" FILTER(")
                    .append(_Constant.SPACE_WHERE);
            for (int i = 0; i < predicateSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                whereList.get(i).appendSql(sqlBuilder, context);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }

        private void outerClauseToString(final StringBuilder builder) {
            final List<_Predicate> whereList = this.whereList;
            final int predicateSize;
            if (whereList == null || (predicateSize = whereList.size()) == 0) {
                return;
            }
            builder.append(" FILTER(")
                    .append(_Constant.SPACE_WHERE);
            for (int i = 0; i < predicateSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(whereList.get(i));
            }
            builder.append(_Constant.SPACE_RIGHT_PAREN);

        }


    }//AggFuncFilterClause

    private static abstract class PostgreAggregateWindowFunction extends PostgreWindowFunction
            implements PostgreWindowFunctions._AggWindowFunc {

        private AggFuncFilterClause filterClause;

        private PostgreAggregateWindowFunction(String name, TypeMeta returnType) {
            super(name, returnType);
        }

        @Override
        public final PostgreWindowFunctions._PgAggWindowFuncSpec filter(Consumer<Statement._SimpleWhereClause> consumer) {
            return this.doFilter(true, consumer);
        }

        @Override
        public final PostgreWindowFunctions._PgAggWindowFuncSpec ifFilter(final Consumer<Statement._SimpleWhereClause> consumer) {
            return this.doFilter(false, consumer);
        }


        @Override
        final void appendClauseBeforeOver(final StringBuilder sqlBuilder, final _SqlContext context) {
            final AggFuncFilterClause clause = this.filterClause;
            if (clause != null) {
                clause.appendOuterClause(sqlBuilder, context);
            }
        }

        @Override
        final void outerClauseToString(final StringBuilder builder) {
            final AggFuncFilterClause clause = this.filterClause;
            if (clause != null) {
                clause.outerClauseToString(builder);
            }

        }

        private PostgreAggregateWindowFunction doFilter(final boolean required,
                                                        final Consumer<Statement._SimpleWhereClause> consumer) {
            if (this.filterClause != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final AggFuncFilterClause clause;
            clause = new AggFuncFilterClause(this.outerContext);
            this.filterClause = clause;
            if (required) {
                clause.filter(consumer);
            } else {
                clause.ifFilter(consumer);
            }
            return this;
        }


    }//PostgreAggregateWindowFunction

    private static final class OneArgAggWindowFunc extends PostgreAggregateWindowFunction {

        private final ArmyExpression one;

        /**
         * @see #oneArgAggWindowFunc(String, Expression, TypeMeta)
         */
        private OneArgAggWindowFunc(String name, Expression one, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
        }

        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            this.one.appendSql(sqlBuilder, context);
        }

        @Override
        void argToString(StringBuilder builder) {
            builder.append(this.one);
        }


    }//OneArgAggWindowFunc

    private static final class TwoArgAggWindowFunc extends PostgreAggregateWindowFunction {

        private final ArmyExpression one;

        private final ArmyExpression two;

        /**
         * @see #twoArgAggWindowFunc(String, Expression, Expression, TypeMeta)
         */
        private TwoArgAggWindowFunc(String name, Expression one, Expression two, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            this.one.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            this.two.appendSql(sqlBuilder, context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two);
        }


    }//OneArgAggWindowFunc

    /**
     * <p>
     * This class is base class of :
     * <ul>
     *     <li>{@link NonOrderedSetAggregateFunction}</li>
     *     <li>{@link PgWithGroupAggFunc}</li>
     * </ul>
     */
    private static abstract class PostgreAggregateFunction extends OperationExpression.SqlFunctionExpression
            implements PostgreWindowFunctions._PgAggFunc, FunctionOuterClause {

        final CriteriaContext outerContext;

        private AggFuncFilterClause filterClause;

        private PostgreAggregateFunction(String name, boolean buildIn, TypeMeta returnType,
                                         CriteriaContext outerContext) {
            super(name, buildIn, returnType);
            this.outerContext = outerContext;
        }

        @Override
        public final SimpleExpression filter(Consumer<Statement._SimpleWhereClause> consumer) {
            return this.doFilter(true, consumer);
        }

        @Override
        public final SimpleExpression ifFilter(final Consumer<Statement._SimpleWhereClause> consumer) {
            return this.doFilter(false, consumer);
        }

        @Override
        public final void appendFuncRest(final StringBuilder sqlBuilder, final _SqlContext context) {
            if (this instanceof PgWithGroupAggFunc) {
                ((PgWithGroupAggFunc) this).appendWithGroupClause(sqlBuilder, context);
            }
            final AggFuncFilterClause filterClause = this.filterClause;
            if (filterClause != null) {
                filterClause.appendOuterClause(sqlBuilder, context);
            }
        }

        @Override
        public final void funcRestToString(final StringBuilder builder) {
            if (this instanceof PgWithGroupAggFunc) {
                ((PgWithGroupAggFunc) this).withGroupClauseToString(builder);
            }
            final AggFuncFilterClause filterClause = this.filterClause;
            if (filterClause != null) {
                builder.append(filterClause);
            }
        }

        private SimpleExpression doFilter(final boolean required, final Consumer<Statement._SimpleWhereClause> consumer) {
            if (this.filterClause != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final AggFuncFilterClause clause;
            clause = new AggFuncFilterClause(this.outerContext);
            this.filterClause = clause;
            if (required) {
                clause.filter(consumer);
            } else {
                clause.ifFilter(consumer);
            }
            return this;
        }


    }//PostgreAggregateFunction


    /**
     * This class is base class of non-window aggregate function.
     */
    private static abstract class NonOrderedSetAggregateFunction extends PostgreAggregateFunction
            implements PostgreWindowFunctions._PgAggFunc {

        private final SQLs.ArgDistinct modifier;

        private final OrderByOptionClause orderByClause;

        private NonOrderedSetAggregateFunction(String name, boolean buildIn, @Nullable SQLs.ArgDistinct modifier,
                                               final @Nullable OrderByOptionClause orderByClause, TypeMeta returnType) {
            super(name, buildIn, returnType, orderByClause == null ? ContextStack.peek() : orderByClause.context);
            if (!(modifier == null || modifier instanceof SQLs.ArmyKeyWord)) {
                throw CriteriaUtils.funcArgError(name, modifier);
            }
            this.modifier = modifier;
            this.orderByClause = orderByClause;
        }


        @Override
        final void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            final SQLs.ArgDistinct modifier = this.modifier;
            if (modifier != null) {
                sqlBuilder.append(modifier.spaceRender());
            }

            this.pgAppendArg(sqlBuilder, context);

            final OrderByOptionClause orderByClause = this.orderByClause;
            if (orderByClause != null) {
                orderByClause.appendSql(sqlBuilder, context);
            }
        }

        @Override
        final void argToString(final StringBuilder builder) {
            final SQLs.ArgDistinct modifier = this.modifier;
            if (modifier != null) {
                builder.append(modifier.spaceRender());
            }

            this.pgArgToString(builder);

            final OrderByOptionClause orderByClause = this.orderByClause;
            if (orderByClause != null) {
                builder.append(orderByClause);
            }
        }


        abstract void pgAppendArg(final StringBuilder sqlBuilder, final _SqlContext context);


        abstract void pgArgToString(final StringBuilder builder);


    }//PostgreAggregateFunction

    private static final class OneArgAggFunc extends NonOrderedSetAggregateFunction {

        private final ArmyExpression one;

        /**
         * @see #_oneArgAggFunc(String, boolean, SQLs.ArgDistinct, Expression, Consumer, TypeMeta)
         */
        private OneArgAggFunc(String name, boolean buildIn, @Nullable SQLs.ArgDistinct modifier, Expression one,
                              @Nullable OrderByOptionClause orderByClause, TypeMeta returnType) {
            super(name, buildIn, modifier, orderByClause, returnType);
            this.one = (ArmyExpression) one;
        }

        @Override
        void pgAppendArg(StringBuilder sqlBuilder, _SqlContext context) {
            this.one.appendSql(sqlBuilder, context);
        }

        @Override
        void pgArgToString(StringBuilder builder) {
            builder.append(this.one);
        }


    }//OneArgAggFunc

    private static final class TwoArgAggFunc extends NonOrderedSetAggregateFunction {

        private final ArmyExpression one;

        private final ArmyExpression two;

        /**
         * @see #_twoArgAggFunc(String, boolean, SQLs.ArgDistinct, Expression, Expression, Consumer, TypeMeta)
         */
        private TwoArgAggFunc(String name, boolean buildIn, @Nullable SQLs.ArgDistinct modifier, Expression one, Expression two,
                              @Nullable OrderByOptionClause orderByClause, TypeMeta returnType) {
            super(name, buildIn, modifier, orderByClause, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
        }

        @Override
        void pgAppendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            this.one.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            this.two.appendSql(sqlBuilder, context);
        }

        @Override
        void pgArgToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two);
        }


    }//TwoArgAggFunc

    private static abstract class PgWithGroupAggFunc extends PostgreAggregateFunction
            implements PostgreWindowFunctions._AggWithGroupClause {

        private List<ArmySortItem> orderByItemList;

        private PgWithGroupAggFunc(String name, boolean buildIn, TypeMeta returnType) {
            super(name, buildIn, returnType, ContextStack.peek());
        }

        @Override
        public final PostgreWindowFunctions._PgAggFunc withinGroup(Consumer<Statement._SimpleOrderByClause> consumer) {
            if (this.orderByItemList != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final OrderByOptionClause clause;
            clause = FunctionUtils.orderByOptionClause(this.outerContext);
            consumer.accept(clause);

            final List<ArmySortItem> list;
            list = clause.endOrderByClauseIfNeed();
            this.orderByItemList = list;
            if (list.size() == 0) {
                throw CriteriaUtils.dontAddAnyItem();
            }

            final TypeMeta returnType = this.returnType;
            if (returnType instanceof OrderedSetType) {
                ((OrderedSetType) returnType).onOrderByEnd(list.get(0));
            }
            return this;
        }


        private void appendWithGroupClause(final StringBuilder sqlBuilder, final _SqlContext context) {
            final List<ArmySortItem> list = this.orderByItemList;
            final int itemSize;
            if (list == null || (itemSize = list.size()) == 0) {
                throw _Exceptions.castCriteriaApi();
            }
            sqlBuilder.append(" WITHIN GROUP(")
                    .append(_Constant.SPACE_ORDER_BY);
            for (int i = 0; i < itemSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                list.get(i).appendSql(sqlBuilder, context);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }

        private void withGroupClauseToString(final StringBuilder builder) {
            final List<ArmySortItem> list = this.orderByItemList;
            final int itemSize;
            if (list == null || (itemSize = list.size()) == 0) {
                throw _Exceptions.castCriteriaApi();
            }
            builder.append(" WITHIN GROUP(")
                    .append(_Constant.SPACE_ORDER_BY);
            for (int i = 0; i < itemSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(list.get(i));
            }
            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }


    }//PgWithGroupAggFunc


    private static final class ZeroArgWithGroupAggFunc extends PgWithGroupAggFunc
            implements NoArgFunction {

        /**
         * @see #zeroArgWithGroupAggFunc(String, TypeMeta)
         * @see #zeroArgMyWithGroupAggFunc(String, TypeMeta)
         */
        private ZeroArgWithGroupAggFunc(String name, boolean buildIn, TypeMeta returnType) {
            super(name, buildIn, returnType);
        }

        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            //no-op
        }

        @Override
        void argToString(StringBuilder builder) {
            //no-op
        }


    }//ZeroArgWithGroupAggFunc

    private static final class OneArgWithGroupAggFunc extends PgWithGroupAggFunc {

        private final ArmyExpression one;

        /**
         * @see #oneArgWithGroupAggFunc(String, Expression, TypeMeta)
         * @see #oneArgMyWithGroupAggFunc(String, Expression, TypeMeta)
         */
        private OneArgWithGroupAggFunc(String name, boolean buildIn, Expression one, TypeMeta returnType) {
            super(name, buildIn, returnType);
            this.one = (ArmyExpression) one;
        }

        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            this.one.appendSql(sqlBuilder, context);
        }

        @Override
        void argToString(StringBuilder builder) {
            builder.append(this.one);
        }


    }//OneArgWithGroupAggFunc

    private static final class MultiArgWithGroupAggFunc extends PgWithGroupAggFunc {

        private final List<? extends ArmyExpression> argList;

        private MultiArgWithGroupAggFunc(String name, boolean buildIn, List<? extends ArmyExpression> argList,
                                         TypeMeta returnType) {
            super(name, buildIn, returnType);
            this.argList = argList;
        }

        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            FunctionUtils.appendArguments(null, this.argList, context);
        }

        @Override
        void argToString(StringBuilder builder) {
            FunctionUtils.argumentsToString(null, this.argList, builder);
        }


    }//MultiArgWithGroupAggFunc


    private interface OrderedSetType {

        void onOrderByEnd(TypeInfer inputType);

    }

    private static final class UnaryOrderedSetType implements TypeMeta, OrderedSetType {

        private final UnaryOperator<MappingType> function;

        private TypeInfer inputType;

        private MappingType type;

        private UnaryOrderedSetType(UnaryOperator<MappingType> function) {
            this.function = function;
        }

        @Override
        public void onOrderByEnd(final TypeInfer inputType) {
            if (this.inputType != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.inputType = inputType;
            this.mappingType();
        }

        @Override
        public MappingType mappingType() {
            MappingType type = this.type;
            if (type != null) {
                return type;
            }
            final TypeInfer inputType = this.inputType;
            if (inputType == null) {
                throw noWithGroupClause();
            } else if (inputType instanceof MappingType) {
                type = (MappingType) inputType;
            } else if (inputType instanceof TableField) {
                type = ((TableField) inputType).mappingType();
            } else {
                type = inputType.typeMeta().mappingType();
            }

            type = this.function.apply(type);
            this.type = type;
            return type;
        }


    }//UnaryOrderedSetType

    private static final class BiOrderedSetType implements TypeMeta, OrderedSetType {

        private final TypeInfer argInfer;

        private final BinaryOperator<MappingType> function;

        private TypeInfer inputInfer;

        private MappingType type;

        private BiOrderedSetType(TypeInfer argInfer, BinaryOperator<MappingType> function) {
            this.argInfer = argInfer;
            this.function = function;
        }

        @Override
        public void onOrderByEnd(final TypeInfer inputType) {
            if (this.inputInfer != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.inputInfer = inputType;
            this.mappingType();
        }

        @Override
        public MappingType mappingType() {
            MappingType type = this.type;
            if (type != null) {
                return type;
            }
            final TypeInfer argInfer = this.argInfer, inputInfer = this.inputInfer;

            if (inputInfer == null) {
                throw noWithGroupClause();
            }
            final MappingType argType, inputType;

            if (argInfer instanceof MappingType) {
                argType = (MappingType) argInfer;
            } else if (inputInfer instanceof TableField) {
                argType = ((TableField) argInfer).mappingType();
            } else {
                argType = argInfer.typeMeta().mappingType();
            }

            if (inputInfer instanceof MappingType) {
                inputType = (MappingType) inputInfer;
            } else if (inputInfer instanceof TableField) {
                inputType = ((TableField) inputInfer).mappingType();
            } else {
                inputType = inputInfer.typeMeta().mappingType();
            }

            type = this.function.apply(argType, inputType);
            this.type = type;
            return type;
        }


    }//BiOrderedSetType


    private static final class PostgreRowsFromFunction implements Functions._TabularWithOrdinalityFunction,
            Postgres._RowsFromSpaceClause,
            Postgres._RowsFromCommaClause,
            Postgres.RowFromConsumer,
            ArmyTabularFunction {

        private static final String ROWS_FROM = "ROWS FROM";

        private List<ArmySQLFunction> functionList = _Collections.arrayList();

        private List<Selection> fieldList = _Collections.arrayList();

        private Map<String, Selection> fieldMap = _Collections.hashMap();

        private boolean existsAnonymousField;

        private Boolean state;

        /**
         * @see #rowsFrom(Consumer)
         */
        private PostgreRowsFromFunction() {
        }

        @Override
        public String name() {
            return ROWS_FROM;
        }

        @Override
        public Postgres._RowsFromCommaClause space(SimpleExpression func) {
            if (this.state != null) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.state = Boolean.TRUE;
            return this.onAddFuncExp(func);
        }

        @Override
        public Postgres._RowsFromCommaClause space(SimplePredicate func) {
            if (this.state != null) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.state = Boolean.TRUE;
            return this.onAddFuncExp(func);
        }

        @Override
        public Postgres._RowsFromCommaClause space(Functions._TabularFunction func) {
            if (this.state != null) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.state = Boolean.TRUE;
            return this.onAddFuncExp(func);
        }

        @Override
        public Postgres._RowsFromAsClause space(UndoneFunction func) {
            if (this.state != null) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.state = Boolean.TRUE;
            return this.comma(func);
        }


        @Override
        public Postgres._RowsFromCommaClause comma(SimpleExpression func) {
            return this.onAddFuncExp(func);
        }

        @Override
        public Postgres._RowsFromCommaClause comma(SimplePredicate func) {
            return this.onAddFuncExp(func);
        }

        @Override
        public Postgres._RowsFromCommaClause comma(Functions._TabularFunction func) {
            return this.onAddFuncExp(func);
        }

        @Override
        public Postgres._RowsFromAsClause comma(final UndoneFunction func) {
            final Boolean state = this.state;
            if (state != Boolean.TRUE) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (!(func instanceof ArmySQLFunction)) {
                throw ContextStack.clearStackAndNonArmyItem(func);
            }
            return consumer -> PostgreUtils.rowsFromUndoneFunc(func, this::onFuncDone).apply(consumer);
        }

        @Override
        public Postgres.RowFromConsumer accept(SimpleExpression func) {
            if (this.state == null) {
                this.state = Boolean.TRUE;
            }
            this.onAddFuncExp(func);
            return this;
        }

        @Override
        public Postgres.RowFromConsumer accept(SimplePredicate func) {
            if (this.state == null) {
                this.state = Boolean.TRUE;
            }
            this.onAddFuncExp(func);
            return this;
        }

        @Override
        public Postgres.RowFromConsumer accept(Functions._TabularFunction func) {
            if (this.state == null) {
                this.state = Boolean.TRUE;
            }
            this.onAddFuncExp(func);
            return this;
        }

        @Override
        public Postgres._RowsFromConsumerAsClause accept(final UndoneFunction func) {
            if (this.state == null) {
                this.state = Boolean.TRUE;
            }
            if (!(func instanceof ArmySQLFunction)) {
                throw ContextStack.clearStackAndNonArmyItem(func);
            }
            return consumer -> PostgreUtils.rowsFromUndoneFunc(func, this::onFuncDone).apply(consumer);
        }

        @Override
        public Functions._TabularFunction withOrdinality() {
            if (this.state != Boolean.FALSE) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            final List<Selection> fieldList = this.fieldList;
            if (fieldList.get(fieldList.size() - 1) == TabularSqlFunction.ORDINALITY_FIELD) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }

            final List<Selection> temp;
            if (fieldList instanceof ArrayList) {
                temp = fieldList;
            } else {
                temp = _Collections.arrayList(fieldList.size() + 1);
                temp.addAll(fieldList);
            }

            temp.add(TabularSqlFunction.ORDINALITY_FIELD);
            if (temp == fieldList) {
                this.fieldList = _Collections.unmodifiableList(temp);
            }
            return this;
        }

        @Override
        public Functions._TabularFunction ifWithOrdinality(BooleanSupplier predicate) {
            if (predicate.getAsBoolean()) {
                this.withOrdinality();
            }
            return this;
        }


        @Override
        public boolean hasAnonymousField() {
            return this.existsAnonymousField;
        }

        @Override
        public boolean hasWithOrdinality() {
            final List<Selection> fieldList = this.fieldList;
            return fieldList.get(fieldList.size() - 1) == TabularSqlFunction.ORDINALITY_FIELD;
        }

        @Override
        public Selection refSelection(final @Nullable String name) {
            if (this.existsAnonymousField) {
                //no bug,never here
                throw new IllegalStateException();
            }
            return this.fieldMap.get(name);
        }

        @Override
        public List<? extends Selection> refAllSelection() {
            List<Selection> fieldList = this.fieldList;
            if (fieldList instanceof ArrayList) {
                fieldList = _Collections.unmodifiableList(fieldList);
                this.fieldList = fieldList;
            }
            return fieldList;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE)
                    .append(ROWS_FROM)
                    .append(_Constant.LEFT_PAREN);
            CriteriaUtils.appendSelfDescribedList(this.functionList, sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


        @Override
        public String toString() {
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(ROWS_FROM)
                    .append(_Constant.LEFT_PAREN);
            CriteriaUtils.selfDescribedListToString(this.functionList, sqlBuilder);
            return sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }

        private Postgres._RowsFromCommaClause onAddFuncExp(final @Nullable Item func) {
            final Boolean state = this.state;
            final List<ArmySQLFunction> funcList = this.functionList;
            final List<Selection> fieldList = this.fieldList;

            if (state != Boolean.TRUE || !(funcList instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (!(func instanceof ArmySQLFunction)) {
                throw ContextStack.clearStackAndNonArmyItem(func);
            } else if (func instanceof PostgreRowsFromFunction) {
                throw ContextStack.clearStackAndCriteriaError("Don't support ROWS FROM() inside ROWS FROM()");
            } else if (func instanceof Expression) {
                this.existsAnonymousField = true;
                fieldList.add(ArmySelections.forAnonymous(((Expression) func).typeMeta()));
            } else if (func instanceof Functions._TabularFunction) {
                if (((ArmyTabularFunction) func).hasWithOrdinality()) {
                    throw ContextStack.clearStackAndCriteriaError("Don't support WITH ORDINALITY inside ROWS FROM()");
                }

                final boolean anonymous;
                anonymous = ((ArmyTabularFunction) func).hasAnonymousField();
                this.existsAnonymousField = anonymous;

                if (anonymous) {
                    fieldList.addAll(((ArmyTabularFunction) func).refAllSelection());
                } else {
                    final Map<String, Selection> fieldMap = this.fieldMap;
                    boolean notDuplicate = true;
                    for (Selection field : ((ArmyTabularFunction) func).refAllSelection()) {

                        fieldList.add(field);

                        if (notDuplicate && fieldMap.putIfAbsent(field.label(), field) != null) {
                            notDuplicate = false;
                            this.existsAnonymousField = true;
                        }
                    }
                }// else

            } else {
                //no bug,never here
                throw ContextStack.clearStackAndCriteriaError(String.format("unknown function[%s]", func));
            }

            funcList.add((ArmySQLFunction) func);
            return this;
        }

        private PostgreRowsFromFunction onFuncDone(final PostgreUtils.DoneFunc func) {
            final List<ArmySQLFunction> funcList = this.functionList;
            if (!(funcList instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            funcList.add(func);

            final List<Selection> fieldList = this.fieldList;
            final Map<String, Selection> fieldMap = this.fieldMap;
            boolean notDuplicate = true;
            for (_FunctionField field : func.fieldList) {

                fieldList.add(field);

                if (notDuplicate && fieldMap.putIfAbsent(field.fieldName(), field) != null) {
                    notDuplicate = false;
                    this.existsAnonymousField = true;
                }

            }
            return this;
        }


        private PostgreRowsFromFunction endFunc() {
            this.state = Boolean.FALSE;
            final List<ArmySQLFunction> funcList = this.functionList;
            if (funcList.size() == 0) {
                throw CriteriaUtils.dontAddAnyItem();
            }
            this.functionList = _Collections.unmodifiableList(funcList);
            if (this.existsAnonymousField) {
                this.fieldMap = null;
            } else {
                this.fieldMap = _Collections.unmodifiableMap(this.fieldMap);
            }
            return this;
        }


    }//PostgreRowsFromSpaceClause


}
