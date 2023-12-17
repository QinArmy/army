package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.TypeDef;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._Constant;
import io.army.dialect._DialectUtils;
import io.army.dialect._SqlContext;
import io.army.meta.MetaException;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.util.Locale;

public abstract class TypeDefs implements TypeDef {

    public static TypeDef space(final DataType type, final int precision) {
        if (precision < 0) {
            throw ContextStack.clearStackAndCriteriaError(String.format("precision %s error", type));
        }
        if (!(type instanceof SqlType)) {
            return new TypeDefLength(type, precision);
        }
        if (type instanceof MySQLType) {
            checkMySqlTypePrecision((MySQLType) type);
        } else if (type instanceof PostgreType) {
            checkPostgreTypePrecision((PostgreType) type);
        } else {
            throw ContextStack.clearStackAndCriteriaError(String.format("unknown %s", type));
        }
        return new TypeDefLength(type, precision);
    }


    private static void checkMySqlTypePrecision(final MySQLType type) {
        switch (type) {
            case DECIMAL:
            case DECIMAL_UNSIGNED:

            case CHAR:
            case VARCHAR:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:

            case TIME:
            case DATETIME:

            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:

            case BIT:
                break;
            default:
                throw dontSupportPrecision(type);
        }
    }


    private static void checkPostgreTypePrecision(final PostgreType type) {
        switch (type) {
            case DECIMAL:

            case CHAR:
            case VARCHAR:

            case BIT:
            case VARBIT:

            case TIME:
            case TIMETZ:

            case TIMESTAMP:
            case TIMESTAMPTZ:
                break;
            default:
                throw dontSupportPrecision(type);
        }
    }


    private static CriteriaException dontSupportPrecision(SqlType type) {
        return ContextStack.clearStackAndCriteriaError(String.format("%s don't support precision", type));
    }

    final DataType dataType;

    private TypeDefs(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public final String typeName() {
        return this.dataType.typeName();
    }

    private static final class TypeDefLength extends TypeDefs implements _SelfDescribed {

        private final int length;

        private TypeDefLength(DataType dataType, int length) {
            super(dataType);
            this.length = length;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE);

            final DataType type = this.dataType;
            final String typeName = type.typeName();
            final int spaceIndex;
            if (!(type instanceof SqlType)) {
                if (context.parser().isKeyWords(typeName.toUpperCase(Locale.ROOT))) {
                    throw new CriteriaException(String.format("typeName of %s is key word", type));
                } else if (_DialectUtils.isSimpleIdentifier(typeName)) {
                    sqlBuilder.append(typeName);
                } else {
                    context.identifier(typeName, sqlBuilder);
                }
                spaceIndex = -1;
            } else if (((SqlType) type).armyType().isUnsigned()) {
                spaceIndex = typeName.indexOf(" UNSIGNED");
                if (spaceIndex < 1) {
                    throw new MetaException(String.format("%s typeName error", type));
                }
                sqlBuilder.append(typeName, 0, spaceIndex);
            } else {
                sqlBuilder.append(typeName);
                spaceIndex = -1;
            }

            sqlBuilder.append(_Constant.LEFT_PAREN)
                    .append(this.length)
                    .append(_Constant.RIGHT_PAREN);

            if (spaceIndex > 1) {
                sqlBuilder.append(typeName, spaceIndex, typeName.length());
            }
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(_Constant.SPACE);

            final DataType type = this.dataType;
            final String typeName = type.typeName();
            final int spaceIndex;
            if (!(type instanceof SqlType)) {
                builder.append(typeName);
                spaceIndex = -1;
            } else if (((SqlType) type).armyType().isUnsigned()) {
                spaceIndex = typeName.indexOf(" UNSIGNED");
                if (spaceIndex < 1) {
                    throw new MetaException(String.format("%s typeName error", type));
                }
                builder.append(typeName, 0, spaceIndex);
            } else {
                builder.append(typeName);
                spaceIndex = -1;
            }

            builder.append(_Constant.LEFT_PAREN)
                    .append(this.length)
                    .append(_Constant.RIGHT_PAREN);

            if (spaceIndex > 1) {
                builder.append(typeName, spaceIndex, typeName.length());
            }
            return builder.toString();
        }


    } // TypeDefLength


}
