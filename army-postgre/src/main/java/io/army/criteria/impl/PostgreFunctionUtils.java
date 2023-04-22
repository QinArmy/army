package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._DialectUtils;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingType;
import io.army.mapping.TextType;
import io.army.mapping.XmlType;
import io.army.meta.TypeMeta;
import io.army.sqltype.PgSqlType;
import io.army.util._Collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
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


    private static SimpleExpression onXmlForestEnd(ArmyFuncClause clause) {
        assert clause instanceof XmlNamedElementPart;
        return FunctionUtils.clauseFunc("XMLFOREST", clause, XmlType.TEXT_INSTANCE);
    }


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


    private static final class XmlTableColumnsClause implements Postgres._XmlTableColumnsClause,
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
        public void appendSql(_SqlContext context) {

        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, PgSqlType type, Functions.WordPath path,
                                                    Expression columnExp, SqlSyntax.WordDefault wordDefault,
                                                    Expression defaultExp,
                                                    SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, path, columnExp, wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, PgSqlType type, SqlSyntax.WordDefault wordDefault,
                                                    Expression defaultExp, SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, PgSqlType type, Functions.WordPath path,
                                                    Expression columnExp, SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, path, columnExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, PgSqlType type, Functions.WordPath path,
                                                    Expression columnExp, SqlSyntax.WordDefault wordDefault,
                                                    Expression defaultExp) {
            return this.comma(name, type, path, columnExp, wordDefault, defaultExp);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, PgSqlType type, SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, PgSqlType type, SqlSyntax.WordDefault wordDefault,
                                                    Expression defaultExp) {
            return this.comma(name, type, wordDefault, defaultExp);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, PgSqlType type, Functions.WordPath path,
                                                    Expression columnExp) {
            return this.comma(name, type, path, columnExp);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, PgSqlType type) {
            return this.comma(name, type);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, Functions.WordsForOrdinality forOrdinality) {
            return this.comma(name, forOrdinality);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, PgSqlType type, Functions.WordPath path,
                                                    BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                    String columnExp, SqlSyntax.WordDefault wordDefault,
                                                    Expression defaultExp, SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, path, funcRefForColumnExp, columnExp, wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, PgSqlType type, Functions.WordPath path,
                                                    BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                    String columnExp, SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, path, funcRefForColumnExp, columnExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause columns(String name, PgSqlType type, Functions.WordPath path,
                                                    BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                    String columnExp, SqlSyntax.WordDefault wordDefault,
                                                    Expression defaultExp) {
            return this.comma(name, type, path, funcRefForColumnExp, columnExp, wordDefault, defaultExp);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, PgSqlType type, Functions.WordPath path,
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
        public Postgres.XmlTableCommaClause comma(String name, PgSqlType type, SqlSyntax.WordDefault wordDefault,
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
        public Postgres.XmlTableCommaClause comma(String name, PgSqlType type, Functions.WordPath path,
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
        public Postgres.XmlTableCommaClause comma(String name, PgSqlType type, Functions.WordPath path,
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
        public Postgres.XmlTableCommaClause comma(String name, PgSqlType type,
                                                  @Nullable SqlSyntax.NullOption nullOption) {
            if (nullOption == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, Postgres.PATH, null, SQLs.DEFAULT, null, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, PgSqlType type, SqlSyntax.WordDefault wordDefault,
                                                  @Nullable Expression defaultExp) {
            if (defaultExp == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, Postgres.PATH, null, SQLs.DEFAULT, defaultExp, null);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, PgSqlType type, Functions.WordPath path,
                                                  @Nullable Expression columnExp) {
            if (columnExp == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return this.onAdd(name, type, Postgres.PATH, columnExp, SQLs.DEFAULT, null, null);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, PgSqlType type) {
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
        public Postgres.XmlTableCommaClause comma(String name, PgSqlType type, Functions.WordPath path,
                                                  BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                  String columnExp, SqlSyntax.WordDefault wordDefault,
                                                  Expression defaultExp, SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, path, funcRefForColumnExp.apply(TextType.INSTANCE, columnExp),
                    wordDefault, defaultExp, nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, PgSqlType type, Functions.WordPath path,
                                                  BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                  String columnExp, SqlSyntax.NullOption nullOption) {
            return this.comma(name, type, path, funcRefForColumnExp.apply(TextType.INSTANCE, columnExp), nullOption);
        }

        @Override
        public Postgres.XmlTableCommaClause comma(String name, PgSqlType type, Functions.WordPath path,
                                                  BiFunction<MappingType, String, Expression> funcRefForColumnExp,
                                                  String columnExp, SqlSyntax.WordDefault wordDefault,
                                                  Expression defaultExp) {
            return this.comma(name, type, path, funcRefForColumnExp.apply(TextType.INSTANCE, columnExp),
                    wordDefault, defaultExp);
        }


        private Postgres.XmlTableCommaClause onAdd(
                final @Nullable String name, @Nullable final PgSqlType type, final Functions.WordPath path,
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

            return this.onAddColumn(new XmlTableDataColumn(name, null, columnExp, defaultExp, nullOption));
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

        private final MappingType type;


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
        public final String selectionName() {
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
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            context.parser().identifier(this.name, sqlBuilder);

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
                sqlBuilder.append(nullOption.spaceRender());
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


}
