package io.army.criteria.impl;

import io.army.criteria.DataField;
import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.SimpleExpression;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._DialectUtils;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.TextType;
import io.army.mapping.XmlType;
import io.army.util._Collections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

abstract class PostgreFunctionUtils extends FunctionUtils {

    private PostgreFunctionUtils() {
    }


    static XmlNamedElementPart<XmlAttributes> xmlAttributes() {
        return new XmlNamedElementPart<>(XmlAttributes::new);
    }

    static XmlNamedElementPart<SimpleExpression> xmlForest() {
        return new XmlNamedElementPart<>(PostgreFunctionUtils::onXmlForestEnd);
    }

    private static SimpleExpression onXmlForestEnd(ArmyFuncClause clause) {
        assert clause instanceof XmlNamedElementPart;
        return FunctionUtils.clauseFunc("XMLFOREST", clause, XmlType.TEXT_INSTANCE);
    }


    static final class XmlNamedElementPart<R extends Item> implements ArmyFuncClause,
            Postgres._XmlNamedElementPart {


        private final Function<ArmyFuncClause, R> function;

        private final CriteriaContext outerContext;

        private List<Object> attValueList;

        private XmlNamedElementPart(Function<ArmyFuncClause, R> function) {
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
        public Postgres._XmlNamedElementPart accept(final @Nullable DataField field) {
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
        public Postgres._XmlNamedElementPart accept(
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
        public Postgres._XmlNamedElementPart accept(BiFunction<MappingType, String, Expression> funcRef, String attValue, SqlSyntax.WordAs as, String attName) {
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

    static final class XmlAttributes implements ArmyFuncClause, PostgreStringFunctions.XmlAttributes {

        private final ArmyFuncClause clause;

        private XmlAttributes(ArmyFuncClause clause) {
            assert clause instanceof XmlNamedElementPart;
            this.clause = clause;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(" XMLATTRIBUTES(");
            this.clause.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


    }//XmlAttributes


}
