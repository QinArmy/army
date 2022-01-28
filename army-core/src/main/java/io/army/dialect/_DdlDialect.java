package io.army.dialect;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.sqltype.SqlType;
import io.army.util.StringUtils;
import io.army.util.TimeUtils;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public abstract class _DdlDialect implements DdlDialect {

    protected final List<String> errorMsgList = new ArrayList<>();

    protected final _AbstractDialect dialect;

    protected _DdlDialect(_AbstractDialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public final <T extends IDomain> String createTable(final TableMeta<T> table) {
        final _AbstractDialect dialect = this.dialect;
        final StringBuilder builder = new StringBuilder(128)
                .append("CREATE TABLE IF NOT EXISTS ")
                .append(dialect.safeTableName(table.tableName()))
                .append(Constant.SPACE_LEFT_BRACKET)
                .append("\n\t");

        final ServerMeta serverMeta = dialect.environment.serverMeta();

        final List<FieldMeta<T, ?>> fieldList = table.fieldList();
        final int fieldSize = fieldList.size();
        for (int i = 0; i < fieldSize; i++) {
            if (i > 0) {
                builder.append(" ,\n\t");
            }
            final FieldMeta<T, ?> field;
            field = fieldList.get(i);
            builder.append(dialect.safeColumnName(field.columnName()))
                    .append(Constant.SPACE);

            final SqlType sqlType;
            sqlType = field.mappingType().map(serverMeta);

            this.dataType(field, sqlType, builder);
            builder.append(Constant.SPACE);
            this.defaultValue(field, sqlType, builder);
        }

        this.index(table, builder);

        builder.append(Constant.SPACE_RIGHT_BRACKET);
        return builder.toString();
    }


    protected abstract void dataType(FieldMeta<?, ?> field, SqlType type, StringBuilder builder);

    protected abstract void defaultValue(FieldMeta<?, ?> field, SqlType type, StringBuilder builder);

    protected abstract boolean isFunctionOrExp(FieldMeta<?, ?> field, SqlType type);


    private <T extends IDomain> void index(final TableMeta<T> table, final StringBuilder builder) {
        final _AbstractDialect dialect = this.dialect;
        for (IndexMeta<T> index : table.indexes()) {

            builder.append(" ,\n\tINDEX")
                    .append(dialect.quoteIfNeed(index.name()));

            final String type;
            type = index.type();
            if (StringUtils.hasText(type)) {
                builder.append(" USING ")
                        .append(dialect.quoteIfNeed(index.type()));
            }
            builder.append(Constant.SPACE_LEFT_BRACKET);// index left bracket

            final List<IndexFieldMeta<T, ?>> indexFieldList = index.fieldList();
            final int indexFieldSize = indexFieldList.size();
            for (int i = 0; i < indexFieldSize; i++) {
                if (i > 0) {
                    builder.append(Constant.SPACE_COMMA);
                }
                final IndexFieldMeta<T, ?> indexField;
                indexField = indexFieldList.get(i);

                builder.append(Constant.SPACE)
                        .append(dialect.quoteIfNeed(indexField.columnName()));

                final Boolean asc = indexField.fieldAsc();
                if (asc != null) {
                    if (asc) {
                        builder.append(Constant.SPACE_ASC);
                    } else {
                        builder.append(Constant.SPACE_DESC);
                    }
                }
            }
            builder.append(Constant.SPACE_RIGHT_BRACKET); // index right bracket
        }
    }


    protected static void decimalType(final FieldMeta<?, ?> field, final StringBuilder builder) {
        final int precision = field.precision();
        if (precision > 0) {
            builder.append(Constant.LEFT_BRACKET)
                    .append(field.precision());
            final int scale = field.scale();
            if (scale > -1) {
                builder.append(Constant.COMMA)
                        .append(scale);
            }
            builder.append(Constant.RIGHT_BRACKET);
        }

    }


    protected void precision(final FieldMeta<?, ?> field, SqlType type
            , final int max, final StringBuilder builder) {
        final int precision = field.precision();
        if (precision > -1) {
            if (precision > max) {
                String m;
                m = String.format("%s precision[%s] out of [1,%s] error for %s.%s"
                        , field, field.scale(), max, type.getClass().getSimpleName(), type.name());
                this.errorMsgList.add(m);
                return;
            }
            builder.append(Constant.LEFT_BRACKET)
                    .append(precision)
                    .append(Constant.RIGHT_BRACKET);
        }
    }

    protected void timeTypeScale(final FieldMeta<?, ?> field, SqlType type, final StringBuilder builder) {
        final int scale = field.scale();
        if (scale > -1) {
            if (scale > 6) {
                timeScaleError(field, type);
                return;
            }
            builder.append(Constant.LEFT_BRACKET)
                    .append(scale)
                    .append(Constant.RIGHT_BRACKET);
        }
    }

    protected boolean checkQuoteValue(final FieldMeta<?, ?> field, String defaultValue) {
        final char[] array = defaultValue.toCharArray();
        boolean match = false, hasText = false;
        for (char c : array) {
            if (c == Constant.QUOTE) {
                if (!hasText) {
                    match = true;
                }
                checkQuoteClose(field, defaultValue, array);
                break;
            }
            if (!Character.isWhitespace(c)) {
                hasText = true;
            }

        }
        return match;

    }

    protected final void appendIntegerDefault(final FieldMeta<?, ?> field, final SqlType type, final long min
            , long max, final StringBuilder builder) {
        final String defaultValue;
        defaultValue = field.defaultValue();
        if (!checkQuoteValue(field, defaultValue)) {
            try {
                final long value;
                value = Long.parseLong(defaultValue);
                if (value < min || value > max) {
                    defaultValueOutOfNumberRange(field, type, min, max);
                    return;
                }
            } catch (NumberFormatException e) {
                //ignore, not number default value.
            }
        }
        builder.append(defaultValue);
    }

    protected final void appendDecimalDefault(final FieldMeta<?, ?> field, final SqlType type, final boolean unsigned
            , final StringBuilder builder) {
        final String defaultValue;
        defaultValue = field.defaultValue();
        if (!checkQuoteValue(field, defaultValue)) {
            try {
                final BigDecimal value;
                value = new BigDecimal(defaultValue);
                if (unsigned && value.compareTo(BigDecimal.ZERO) < 0) {
                    defaultValueOutOfNumberRange(field, type, BigDecimal.ZERO, null);
                    return;
                }
            } catch (NumberFormatException e) {
                //ignore, not number default value.
            }
        }
        builder.append(defaultValue);
    }

    protected final void appendBooleanDefault(final FieldMeta<?, ?> field, final StringBuilder builder) {
        final String defaultValue;
        defaultValue = field.defaultValue();
        checkQuoteValue(field, defaultValue); // check
        builder.append(defaultValue);
    }

    protected final void appendDatetimeDefault(final FieldMeta<?, ?> field, final StringBuilder builder) {
        final String defaultValue;
        defaultValue = field.defaultValue();
        if (checkQuoteValue(field, defaultValue)) {
            builder.append(defaultValue);
        } else {
            try {
                LocalDateTime.parse(defaultValue, TimeUtils.getDatetimeFormatter(6));
                //literal datetime
                builder.append(Constant.QUOTE)
                        .append(defaultValue)
                        .append(Constant.QUOTE);
            } catch (DateTimeException e) {
                // not literal datetime
                builder.append(defaultValue);
            }
        }

    }

    protected final void appendDateDefault(final FieldMeta<?, ?> field, final StringBuilder builder) {
        final String defaultValue;
        defaultValue = field.defaultValue();
        if (checkQuoteValue(field, defaultValue)) {
            builder.append(defaultValue);
        } else {
            try {
                LocalDate.parse(defaultValue);
                //literal date
                builder.append(Constant.QUOTE)
                        .append(defaultValue)
                        .append(Constant.QUOTE);
            } catch (DateTimeException e) {
                // not literal date
                builder.append(defaultValue);
            }
        }
    }

    protected final void appendTimeDefault(final FieldMeta<?, ?> field, final StringBuilder builder) {
        final String defaultValue;
        defaultValue = field.defaultValue();
        if (checkQuoteValue(field, defaultValue)) {
            builder.append(defaultValue);
        } else {
            try {
                LocalTime.parse(defaultValue, TimeUtils.getTimeFormatter(6));
                //literal date
                builder.append(Constant.QUOTE)
                        .append(defaultValue)
                        .append(Constant.QUOTE);
            } catch (DateTimeException e) {
                // not literal date
                builder.append(defaultValue);
            }
        }
    }

    protected final void appendTextDefault(final FieldMeta<?, ?> field, SqlType sqlType, final StringBuilder builder) {
        final String defaultValue;
        defaultValue = field.defaultValue();
        if (checkQuoteValue(field, defaultValue) || isFunctionOrExp(field, sqlType)) {
            builder.append(defaultValue);
        } else {
            builder.append(Constant.QUOTE)
                    .append(defaultValue)
                    .append(Constant.QUOTE);
        }

    }

    protected void defaultValueOutOfNumberRange(FieldMeta<?, ?> field, SqlType type, Number min, @Nullable Number max) {
        String m;
        m = String.format("%s default value out of [%s,%s] of %s.%s"
                , field, min, max == null ? "" : max, type.getClass().getSimpleName(), type.name());
        this.errorMsgList.add(m);

    }


    private void checkQuoteClose(final FieldMeta<?, ?> field, String defaultValue, final char[] array) {
        boolean quote = false;
        for (int i = 0, index; i < array.length; i++) {
            if (!quote) {
                if (array[i] == Constant.QUOTE) {
                    quote = true;
                }
                continue;
            }
            index = defaultValue.indexOf(Constant.QUOTE, i);
            if (index < 0) {
                defaultValueQuoteNotClose(field, defaultValue);
                return;
            }
            if (array[i - 1] == Constant.BACK_SLASH) {
                continue;
            }
            quote = false;
        }

        if (quote) {
            defaultValueQuoteNotClose(field, defaultValue);
        }
    }


    private void timeScaleError(FieldMeta<?, ?> field, SqlType sqlType) {
        String m;
        m = String.format("%s scale[%s] error for %s.%s"
                , field, field.scale(), sqlType.getClass().getSimpleName(), sqlType.name());
        this.errorMsgList.add(m);

    }


    private void defaultValueQuoteNotClose(FieldMeta<?, ?> field, String defaultValue) {
        String m;
        m = String.format("%s default value[%s] quote not close.", field, defaultValue);
        this.errorMsgList.add(m);
    }

}
