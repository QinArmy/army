package io.army.criteria.impl;

import io.army.criteria.LiteralExpression;
import io.army.criteria.QualifiedField;
import io.army.criteria.SqlValueParam;
import io.army.criteria.TableField;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.meta.TypeMeta;

import javax.annotation.Nullable;

/**
 * <p>Package class.
 *
 * @since 0.6.4
 */
abstract class MySQLExpressions {

    private MySQLExpressions() {
        throw new UnsupportedOperationException();
    }

    static LiteralExpression textLiteral(final @Nullable String charsetName, final @Nullable String literal,
                                         final @Nullable String collationName) {
        if (charsetName == null) {
            throw ContextStack.clearStackAndCriteriaError("charsetName must non-null");
        } else if (literal == null) {
            throw ContextStack.clearStackAndCriteriaError("literal must non-null");
        }
        return new AnonymousTextLiteral(charsetName, StringType.INSTANCE, literal, collationName);
    }

    static LiteralExpression encodingTextLiteral(final @Nullable String charsetName, final TableField field,
                                                 final @Nullable String literal, final @Nullable String collationName) {
        if (charsetName == null) {
            throw ContextStack.clearStackAndCriteriaError("charsetName must non-null");
        } else if (!field.codec()) {
            String m = String.format("%s isn't codec filed,you should invoke %s.textLiteral() method.",
                    field, MySQLs.class.getName());
            throw ContextStack.clearStackAndCriteriaError(m);
        } else if (!(field.mappingType() instanceof MappingType.SqlStringType)) {
            String m = String.format("%s isn't string type", field);
            throw ContextStack.clearStackAndCriteriaError(m);
        } else if (literal == null) {
            throw ContextStack.clearStackAndCriteriaError("literal must non-null");
        }
        return new AnonymousTextLiteral(charsetName, field, literal, collationName);
    }


    private static final class AnonymousTextLiteral extends OperationExpression.OperationDefiniteExpression
            implements LiteralExpression, SqlValueParam.SingleAnonymousValue {

        private final String charsetName;

        private final TypeMeta type;


        private final String literal;


        private final String collationName;


        private AnonymousTextLiteral(String charsetName, TypeMeta type, String literal, @Nullable String collationName) {
            this.charsetName = charsetName;
            if (type instanceof QualifiedField) {
                this.type = ((QualifiedField<?>) type).fieldMeta();
            } else {
                this.type = type;
            }
            this.literal = literal;
            this.collationName = collationName;
        }

        @Override
        public TypeMeta typeMeta() {
            // here , allow FieldMeta , because this is literal
            return this.type;
        }


        @Override
        public String value() {
            return this.literal;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(" _");
            context.identifier(this.charsetName, sqlBuilder);

            context.appendLiteral(this.type, this.literal);

            final String collationName = this.collationName;
            if (collationName != null) {
                sqlBuilder.append(_Constant.SPACE_COLLATE_SPACE);
                context.identifier(collationName, sqlBuilder);
            }

        }


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(" _")
                    .append(this.charsetName)
                    .append(_Constant.SPACE)
                    .append(_Constant.QUOTE)
                    .append(this.literal)
                    .append(_Constant.QUOTE);

            final String collationName = this.collationName;
            if (collationName != null) {
                builder.append(_Constant.SPACE_COLLATE_SPACE)
                        .append(collationName);
            }
            return builder.toString();
        }


    } // TextLiteralExpression


}
