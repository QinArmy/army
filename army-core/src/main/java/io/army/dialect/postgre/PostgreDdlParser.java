package io.army.dialect.postgre;

import io.army.annotation.GeneratorType;
import io.army.dialect._Constant;
import io.army.dialect._DdlParser;
import io.army.mapping.NoCastTextType;
import io.army.meta.DatabaseObject;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.schema._FieldResult;
import io.army.sqltype.PostgreSqlType;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;

import java.util.List;

final class PostgreDdlParser extends _DdlParser<PostgreParser> {

    static PostgreDdlParser create(PostgreParser dialect) {
        return new PostgreDdlParser(dialect);
    }

    private PostgreDdlParser(PostgreParser dialect) {
        super(dialect);
    }

    @Override
    public void modifyTableComment(TableMeta<?> table, List<String> sqlList) {

    }

    @Override
    public void modifyColumn(List<_FieldResult> resultList, List<String> sqlList) {

    }

    @Override
    public <T> void createIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList) {

    }

    @Override
    public <T> void changeIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList) {

    }

    @Override
    public <T> void dropIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList) {

    }

    @Override
    protected void dataType(final FieldMeta<?> field, final SqlType type, final StringBuilder builder) {
        switch ((PostgreSqlType) type) {
            case UNKNOWN:
                throw _Exceptions.unexpectedEnum((Enum<?>) type);
            case SMALLINT: {
                if (field.generatorType() == GeneratorType.POST) {
                    builder.append("SMALLSERIAL");
                } else {
                    builder.append(type.name());
                }
            }
            break;
            case INTEGER: {
                if (field.generatorType() == GeneratorType.POST) {
                    builder.append("SERIAL");
                } else {
                    builder.append(type.name());
                }
            }
            break;
            case BIGINT: {
                if (field.generatorType() == GeneratorType.POST) {
                    builder.append("BIGSERIAL");
                } else {
                    builder.append(type.name());
                }
            }
            break;
            case DECIMAL: {
                final int precision, scale;
                precision = field.precision();
                scale = field.scale();

            }
            break;
            case TIME:
            case TIMETZ:
            case TIMESTAMP:
            case TIMESTAMPTZ:
            case TIME_ARRAY:
            case TIMETZ_ARRAY:
            case TIMESTAMP_ARRAY:
            case TIMESTAMPTZ_ARRAY: {
                String safeTypeName;
                safeTypeName = type.name();
                final int index;
                if ((index = safeTypeName.lastIndexOf("_ARRAY")) > 0) {
                    safeTypeName = safeTypeName.substring(0, index);
                }
                builder.append(safeTypeName);
                final int fieldScale;
                switch ((fieldScale = field.scale())) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 5:
                    case 6:
                        builder.append(_Constant.LEFT_PAREN)
                                .append(fieldScale)
                                .append(_Constant.RIGHT_PAREN);
                        break;
                    default:
                        this.errorMsgList.add(String.format("%s scale[%s] error.", field, fieldScale));

                }// switch

                if (index > 0) {
                    this.parser.arrayTypeName(safeTypeName, ArrayUtils.dimensionOfType(field.mappingType()), builder);
                }
            }
            break;
            default:
                this.parser.typeName(field.mappingType(), builder);


        }// switch

    }


    @Override
    protected void appendComment(final DatabaseObject object, final StringBuilder builder) {
        builder.append(SPACE_COMMENT)
                .append(_Constant.SPACE_ON);

        if (object instanceof TableMeta) {
            builder.append(" TABLE ");
            this.parser.safeObjectName(object, builder);
        } else if (object instanceof FieldMeta) {
            builder.append(" COLUMN ");
            this.parser.safeObjectName(((FieldMeta<?>) object).tableMeta(), builder);
            builder.append(_Constant.POINT);
            this.parser.safeObjectName(object, builder);
        } else {
            //no bug,never here
            throw new IllegalArgumentException();
        }

        builder.append(" IS ");

        this.parser.literal(NoCastTextType.INSTANCE, object.comment(), builder);

    }

    @Override
    protected void appendPostGenerator(FieldMeta<?> field, StringBuilder builder) {
        //no-op,see this.dataType() method
    }

    @Override
    protected void appendTableOption(TableMeta<?> table, StringBuilder builder) {
        //no-op
    }


}
