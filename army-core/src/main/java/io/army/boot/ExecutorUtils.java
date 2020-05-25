package io.army.boot;

import io.army.DataAccessException;
import io.army.ErrorCode;
import io.army.codec.FieldCodec;
import io.army.codec.FieldCodecReturnException;
import io.army.meta.FieldMeta;

import java.sql.SQLException;


abstract class ExecutorUtils {

    ExecutorUtils() {
        throw new UnsupportedOperationException();
    }


    static FieldCodecReturnException createCodecReturnTypeException(FieldCodec fieldCodec, FieldMeta<?, ?> fieldMeta
            , Object value) {
        return new FieldCodecReturnException("FieldCodec[%s] return value[%s] must error,FieldMeta[%s],"
                , fieldCodec, value, fieldMeta);
    }


    static DataAccessException convertSQLException(SQLException e, String sql) {
        return new DataAccessException(ErrorCode.ACCESS_ERROR, e, "army set param occur error ,sql[%s]", sql);
    }

}
