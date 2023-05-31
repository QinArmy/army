package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._TableNameElement;
import io.army.criteria.postgre.PostgreQuery;
import io.army.criteria.postgre.PostgreWindow;
import io.army.criteria.standard.StandardQuery;
import io.army.dialect.*;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingType;
import io.army.mapping.TextType;
import io.army.mapping.XmlType;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;
import io.army.stmt.SimpleStmt;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

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


    private static SimpleExpression onXmlForestEnd(ArmyFuncClause clause) {
        assert clause instanceof XmlNamedElementPart;
        return FunctionUtils.clauseFunc("XMLFOREST", clause, XmlType.TEXT);
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
        public void appendSql(final _SqlContext context) {
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
        public void appendSql(final _SqlContext context) {
            final List<Object> attValueList = this.attValueList;
            final int attValueSize;
            if (attValueList == null
                    || attValueList instanceof ArrayList
                    || (attValueSize = attValueList.size()) == 0) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }

            final DialectParser parser;
            parser = context.parser();

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            Object attObject;
            _Pair<ArmyExpression, String> attrPair;
            for (int i = 0; i < attValueSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                attObject = attValueList.get(i);
                if (attObject instanceof _Pair) {
                    attrPair = (_Pair<ArmyExpression, String>) attObject;

                    attrPair.first.appendSql(context);
                    sqlBuilder.append(SQLs.AS.spaceRender());
                    sqlBuilder.append(_Constant.SPACE);
                    parser.identifier(attrPair.second, sqlBuilder);

                } else if (attObject instanceof DataField) {
                    ((ArmyExpression) attObject).appendSql(context);
                } else {
                    //no bug,never here
                    throw new IllegalStateException();
                }
            }

        }


        @Override
        public Postgres._XmlNamedElementFieldClause accept(final @Nullable DataField field) {
            if (!this.supportField) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            List<Object> attValueList = this.attValueList;
            if (attValueList == null) {
                attValueList = _Collections.arrayList();
                this.attValueList = attValueList;
            } else if (!(attValueList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.outerContext);
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
                final @Nullable Expression attValue, SqlSyntax.WordAs as, final @Nullable String attName) {

            List<Object> attValueList = this.attValueList;
            if (attValueList == null) {
                attValueList = _Collections.arrayList();
                this.attValueList = attValueList;
            } else if (!(attValueList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.outerContext);
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
        public Postgres._XmlNamedElementFieldClause accept(BiFunction<MappingType, String, Expression> funcRef, String attValue, SqlSyntax.WordAs as, String attName) {
            return this.accept(funcRef.apply(TextType.INSTANCE, attValue), as, attName);
        }

        R endNamedPart() {
            final List<Object> attValueList = this.attValueList;
            if (attValueList == null || attValueList.size() == 0) {
                throw ContextStack.criteriaError(this.outerContext, "You don't add any thing.");
            } else if (attValueList instanceof ArrayList) {
                this.attValueList = _Collections.unmodifiableList(attValueList);
            } else {
                throw ContextStack.castCriteriaApi(this.outerContext);
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
        public final void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);
            this.clause.appendSql(context);
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
            e = ContextStack.criteriaError(context, m);
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
        public void appendSql(final _SqlContext context) {
            final List<XmlTableColumn> columnList = this.columnList;
            final int columnSize;
            if (columnList == null || columnList instanceof ArrayList || (columnSize = columnList.size()) == 0) {
                throw _Exceptions.castCriteriaApi();
            }

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            for (int i = 0; i < columnSize; i++) {
                if (i == 0) {
                    sqlBuilder.append(" COLUMNS");
                } else {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                columnList.get(i).appendSql(context);
            }

        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, Functions.WordPath path,
                                                    Expression columnExp, SqlSyntax.WordDefault wordDefault,
                                                    Expression defaultExp,
                                                    SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, path, columnExp, wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, SqlSyntax.WordDefault wordDefault,
                                                    Expression defaultExp, SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, Functions.WordPath path,
                                                    Expression columnExp, SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, path, columnExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, Functions.WordPath path,
                                                    Expression columnExp, SqlSyntax.WordDefault wordDefault,
                                                    Expression defaultExp) {
            return this.comma(name, type, path, columnExp, wordDefault, defaultExp);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, SqlSyntax.WordDefault wordDefault,
                                                    Expression defaultExp) {
            return this.comma(name, type, wordDefault, defaultExp);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, Functions.WordPath path,
                                                    Expression columnExp) {
            return this.comma(name, type, path, columnExp);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type) {
            return this.comma(name, type);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, Functions.WordsForOrdinality forOrdinality) {
            return this.comma(name, forOrdinality);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, Functions.WordPath path,
                                                    BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                    String columnExp, SqlSyntax.WordDefault wordDefault,
                                                    Expression defaultExp, SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, path, funcRefForColumnExp, columnExp, wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, Functions.WordPath path,
                                                    BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                    String columnExp, SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, path, funcRefForColumnExp, columnExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, MappingType type, Functions.WordPath path,
                                                    BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                    String columnExp, SqlSyntax.WordDefault wordDefault,
                                                    Expression defaultExp) {
            return this.comma(name, type, path, funcRefForColumnExp, columnExp, wordDefault, defaultExp);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, Functions.WordPath path,
                                                  @Nullable Expression columnExp, SqlSyntax.WordDefault wordDefault,
                                                  @Nullable Expression defaultExp,
                                                  @Nullable SqlSyntax.NullOption nullOption) {
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
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, SqlSyntax.WordDefault wordDefault,
                                                  @Nullable Expression defaultExp,
                                                  @Nullable SqlSyntax.NullOption nullOption) {
            if (defaultExp == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (nullOption == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, Postgres.PATH, null, wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, Functions.WordPath path,
                                                  @Nullable Expression columnExp,
                                                  @Nullable SqlSyntax.NullOption nullOption) {
            if (columnExp == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (nullOption == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, path, columnExp, SQLs.DEFAULT, null, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, Functions.WordPath path,
                                                  @Nullable Expression columnExp, SqlSyntax.WordDefault wordDefault,
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
                                                  @Nullable SqlSyntax.NullOption nullOption) {
            if (nullOption == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, Postgres.PATH, null, SQLs.DEFAULT, null, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, SqlSyntax.WordDefault wordDefault,
                                                  @Nullable Expression defaultExp) {
            if (defaultExp == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, Postgres.PATH, null, SQLs.DEFAULT, defaultExp, null);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, Functions.WordPath path,
                                                  @Nullable Expression columnExp) {
            if (columnExp == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, Postgres.PATH, columnExp, SQLs.DEFAULT, null, null);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type) {
            return this.onAdd(name, type, Postgres.PATH, null, SQLs.DEFAULT, null, null);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(@Nullable String name, Functions.WordsForOrdinality forOrdinality) {
            if (name == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (!_DialectUtils.isSimpleIdentifier(name)) {
                throw CriteriaUtils.funcColumnNameIsNotSimpleIdentifier(this.outerContext, XMLTABLE, name);
            } else if (forOrdinality != Postgres.FOR_ORDINALITY) {
                throw CriteriaUtils.funcArgError(XMLTABLE, forOrdinality);
            }
            return this.onAddColumn(new XmlTableOrdinalityColumn(name));
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, Functions.WordPath path,
                                                  BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                  String columnExp, SqlSyntax.WordDefault wordDefault,
                                                  Expression defaultExp, SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, path, funcRefForColumnExp.apply(TextType.INSTANCE, columnExp),
                    wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, Functions.WordPath path,
                                                  BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                  String columnExp, SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, path, funcRefForColumnExp.apply(TextType.INSTANCE, columnExp), nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, MappingType type, Functions.WordPath path,
                                                  BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                  String columnExp, SqlSyntax.WordDefault wordDefault,
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
                throw ContextStack.castCriteriaApi(this.outerContext);
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
                final @Nullable String name, @Nullable final MappingType type, final Functions.WordPath path,
                final @Nullable Expression columnExp, final SqlSyntax.WordDefault wordDefault,
                final @Nullable Expression defaultExp, final @Nullable SqlSyntax.NullOption nullOption) {

            if (name == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (!_DialectUtils.isSimpleIdentifier(name)) {
                throw CriteriaUtils.funcColumnNameIsNotSimpleIdentifier(this.outerContext, XMLTABLE, name);
            } else if (type == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (path != Postgres.PATH) {
                throw CriteriaUtils.funcArgError(XMLTABLE, path);
            } else if (!(columnExp == null || columnExp instanceof OperationExpression)) {
                throw CriteriaUtils.funcArgError(XMLTABLE, columnExp);
            } else if (wordDefault != SQLs.DEFAULT) {
                throw CriteriaUtils.funcArgError(XMLTABLE, wordDefault);
            } else if (!(defaultExp == null || defaultExp instanceof OperationExpression)) {
                throw CriteriaUtils.funcArgError(XMLTABLE, defaultExp);
            } else if (!(nullOption == null || nullOption == SQLs.NULL || nullOption == Postgres.NOT_NULL)) {
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
                throw ContextStack.castCriteriaApi(this.outerContext);
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
        public final void appendSelectItem(final _SqlContext context) {
            // no bug, never here
            throw new UnsupportedOperationException("invoking error");
        }

        @Override
        public final String fieldName() {
            return this.name;
        }

        @Override
        public final String alias() {
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

        private final SqlSyntax.NullOption nullOption;


        private XmlTableDataColumn(String name, MappingType type, @Nullable Expression columnExp,
                                   @Nullable Expression defaultExp,
                                   @Nullable SqlSyntax.NullOption nullOption) {
            super(name, type);
            this.columnExp = (ArmyExpression) columnExp;
            this.defaultExp = (ArmyExpression) defaultExp;
            this.nullOption = nullOption;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final DialectParser parser;
            parser = context.parser();

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            parser.identifier(this.name, sqlBuilder)
                    .append(_Constant.SPACE);

            parser.typeName(this.type, sqlBuilder);

            final ArmyExpression columnExp = this.columnExp, defaultExp = this.defaultExp;
            if (columnExp != null) {
                sqlBuilder.append(Postgres.PATH.spaceRender());
                columnExp.appendSql(context);
            }

            if (defaultExp != null) {
                sqlBuilder.append(((SQLWords) SQLs.DEFAULT).spaceRender());
                defaultExp.appendSql(context);
            }

            final SqlSyntax.NullOption nullOption = this.nullOption;
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
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            context.parser().identifier(this.name, sqlBuilder)
                    .append(Postgres.FOR_ORDINALITY.spaceRender());

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
        public void appendSql(final _SqlContext context) {
            final SimpleStmt stmt;
            stmt = context.parser().select(this.query, this.visible);
            if (stmt.paramGroup().size() > 0) {
                throw new CriteriaException("query expression couldn't have any parameter.");
            }
            context.appendLiteral(this.typeMeta(), stmt.sqlText());
        }


    }//QueryExpression


    private static abstract class PostgreWindowFunction
            extends FunctionUtils.WindowFunction<PostgreWindow._PartitionBySpec>
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
        void appendArguments(StringBuilder sqlBuilder, _SqlContext context) {
            //no-op
        }

        @Override
        void argumentToString(StringBuilder builder) {
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
        void appendArguments(final StringBuilder sqlBuilder, _SqlContext context) {
            this.one.appendSql(context);
        }

        @Override
        void argumentToString(StringBuilder builder) {
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
        void appendArguments(final StringBuilder sqlBuilder, _SqlContext context) {
            this.one.appendSql(context);
            sqlBuilder.append(_Constant.COMMA);
            this.two.appendSql(context);
        }

        @Override
        void argumentToString(StringBuilder builder) {
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
        void appendArguments(final StringBuilder sqlBuilder, _SqlContext context) {
            this.one.appendSql(context);
            sqlBuilder.append(_Constant.COMMA);
            this.two.appendSql(context);
            sqlBuilder.append(_Constant.COMMA);
            this.three.appendSql(context);
        }

        @Override
        void argumentToString(StringBuilder builder) {
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
                throw ContextStack.castCriteriaApi(this.outerContext);
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
                whereList.get(i).appendSql(context);
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
        final void appendOuterClause(final StringBuilder sqlBuilder, final _SqlContext context) {
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
                throw ContextStack.castCriteriaApi(this.outerContext);
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
        void appendArguments(StringBuilder sqlBuilder, _SqlContext context) {
            this.one.appendSql(context);
        }

        @Override
        void argumentToString(StringBuilder builder) {
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
        void appendArguments(final StringBuilder sqlBuilder, final _SqlContext context) {
            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            this.two.appendSql(context);
        }

        @Override
        void argumentToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two);
        }


    }//OneArgAggWindowFunc

    /**
     * This class is base class of non-window aggregate function.
     */
    private static abstract class PostgreAggregateFunction extends FunctionUtils.FunctionExpression
            implements PostgreWindowFunctions._PgAggFunc, OuterClause {

        private final SQLsSyntax.Modifier modifier;

        private final OrderByOptionClause orderByClause;

        private final CriteriaContext outerContext;
        private AggFuncFilterClause filterClause;

        private PostgreAggregateFunction(String name, @Nullable SQLsSyntax.Modifier modifier,
                                         @Nullable OrderByOptionClause orderByClause, TypeMeta returnType) {
            super(name, returnType);
            assert modifier == null || modifier == SQLs.DISTINCT || modifier == SQLs.ALL;
            this.modifier = modifier;
            this.orderByClause = orderByClause;
            this.outerContext = ContextStack.peek();
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
        public final void appendOuterClause(final StringBuilder sqlBuilder, final _SqlContext context) {
            final AggFuncFilterClause clause = this.filterClause;
            if (clause != null) {
                clause.appendOuterClause(sqlBuilder, context);
            }
        }

        @Override
        public final void outerClauseToString(final StringBuilder builder) {
            final AggFuncFilterClause clause = this.filterClause;
            if (clause != null) {
                clause.outerClauseToString(builder);
            }

        }

        @Override
        final void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            final SQLsSyntax.Modifier modifier = this.modifier;
            if (modifier != null) {
                sqlBuilder.append(modifier.spaceRender());
            }

            this.pgAppendArg(sqlBuilder, context);

            final OrderByOptionClause orderByClause = this.orderByClause;
            if (orderByClause != null) {
                orderByClause.appendSql(context);
            }
        }

        @Override
        final void argToString(final StringBuilder builder) {
            final SQLsSyntax.Modifier modifier = this.modifier;
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


        private SimpleExpression doFilter(final boolean required, final Consumer<Statement._SimpleWhereClause> consumer) {
            if (this.filterClause != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
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

    private static final class OneArgAggFunc extends PostgreAggregateFunction {

        private final ArmyExpression one;

        private OneArgAggFunc(String name, @Nullable SQLsSyntax.Modifier modifier, Expression one,
                              @Nullable OrderByOptionClause orderByClause, TypeMeta returnType) {
            super(name, modifier, orderByClause, returnType);
            this.one = (ArmyExpression) one;
        }

        @Override
        void pgAppendArg(StringBuilder sqlBuilder, _SqlContext context) {
            this.one.appendSql(context);
        }

        @Override
        void pgArgToString(StringBuilder builder) {
            builder.append(this.one);
        }


    }//OneArgAggFunc

    private static final class TwoArgAggFunc extends PostgreAggregateFunction {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private TwoArgAggFunc(String name, @Nullable SQLsSyntax.Modifier modifier, Expression one, Expression two,
                              @Nullable OrderByOptionClause orderByClause, TypeMeta returnType) {
            super(name, modifier, orderByClause, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
        }

        @Override
        void pgAppendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            this.two.appendSql(context);
        }

        @Override
        void pgArgToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two);
        }


    }//TwoArgAggFunc


}
