package io.army.criteria.impl;

import io.army.criteria.DataField;
import io.army.criteria.Expression;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.util._Collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

abstract class PostgreFunctionUtils extends FunctionUtils {

    private PostgreFunctionUtils() {
    }


    static XmlAttributes xmlAttributes() {
        return new XmlAttributes();
    }


    static final class XmlAttributes implements ArmyFuncClause,
            PostgreStringFunctions.XmlAttributes,
            Postgres._XmlAttributeConsumer {

        private static final String SPACE_XML_ATTRIBUTES = " XMLATTRIBUTES";

        private final CriteriaContext outerContext;

        private List<Object> attValueList;

        private XmlAttributes() {
            this.outerContext = ContextStack.peek();
        }


        @SuppressWarnings("unchecked")
        @Override
        public void appendSql(final _SqlContext context) {
            final List<Object> attValueList = this.attValueList;
            final int attValueSize;
            if (attValueList == null || attValueList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if ((attValueSize = attValueList.size()) == 0) {
                return;
            }

            final DialectParser parser;
            parser = context.parser();

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(SPACE_XML_ATTRIBUTES)
                    .append(_Constant.LEFT_PAREN);

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
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }


        @Override
        public Postgres._XmlAttributeConsumer accept(final @Nullable DataField field) {
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
        public Postgres._XmlAttributeConsumer accept(
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
                throw CriteriaUtils.funcArgError(SPACE_XML_ATTRIBUTES, attValue);
            } else if (attName == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            attValueList.add(_Pair.create((ArmyExpression) attValue, attName));
            return this;
        }

        @Override
        public Postgres._XmlAttributeConsumer accept(BiFunction<MappingType, String, Expression> funcRef, String attValue, SqlSyntax.WordAs as, String attName) {
            return this.accept(funcRef.apply(StringType.INSTANCE, attValue), as, attName);
        }

        PostgreStringFunctions.XmlAttributes endXmlAttributes() {
            final List<Object> attValueList = this.attValueList;
            if (attValueList == null) {
                this.attValueList = Collections.emptyList();
            } else if (attValueList instanceof ArrayList) {
                this.attValueList = _Collections.unmodifiableList(attValueList);
            } else {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            return this;
        }


    }//XmlAttributesFunc


}
