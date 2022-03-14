package io.army.example.validate;


import io.army.dialect.Constant;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

public abstract class Validates {

    protected Validates() {
        throw new UnsupportedOperationException();
    }


    public static String standardInsert(TableMeta<?> table, List<FieldMeta<?>> fieldList) {
        final StringBuilder builder = new StringBuilder();
        builder.append(Constant.INSERT_INTO)
                .append(Constant.SPACE)
                .append(table.tableName())
                .append(Constant.SPACE_LEFT_BRACKET);

        final int size = fieldList.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(Constant.SPACE_COMMA);
            }
            final FieldMeta<?> field = fieldList.get(i);
            builder.append(Constant.SPACE)
                    .append(field.columnName());
        }

        return builder.append(Constant.SPACE_RIGHT_BRACKET)
                .append(Constant.SPACE_VALUES)
                .toString();
    }


}
