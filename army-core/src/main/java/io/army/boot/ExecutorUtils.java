package io.army.boot;

import io.army.GenericSessionFactory;
import io.army.UnKnownTypeException;
import io.army.codec.FieldCodec;
import io.army.codec.FieldCodecReturnException;
import io.army.criteria.FieldSelection;
import io.army.criteria.Selection;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.mapping.MappingMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


abstract class ExecutorUtils {

    ExecutorUtils() {
        throw new UnsupportedOperationException();
    }


    static FieldCodecReturnException createCodecReturnTypeException(FieldCodec fieldCodec, FieldMeta<?, ?> fieldMeta
            , Object value) {
        return new FieldCodecReturnException("FieldCodec[%s] return value[%s] must error,FieldMeta[%s],"
                , fieldCodec, value, fieldMeta);
    }


    static int obtainVendorTypeNumber(ParamMeta paramMeta) {
        MappingMeta mappingMeta;
        if (paramMeta instanceof FieldMeta) {
            mappingMeta = ((FieldMeta<?, ?>) paramMeta).mappingMeta();
        } else if (paramMeta instanceof MappingMeta) {
            mappingMeta = (MappingMeta) paramMeta;
        } else {
            throw new UnKnownTypeException(paramMeta);
        }
        return mappingMeta.jdbcType().getVendorTypeNumber();
    }

    static Map<FieldMeta<?, ?>, FieldCodec> createCodecMap(List<Selection> selectionList
            , GenericSessionFactory sessionFactory) {

        Map<FieldMeta<?, ?>, FieldCodec> codecMap = new HashMap<>();

        for (Selection selection : selectionList) {

            if (!(selection instanceof FieldSelection)) {
                continue;
            }
            FieldMeta<?, ?> fieldMeta = ((FieldSelection) selection).fieldMeta();

            FieldCodec fieldCodec = sessionFactory.fieldCodec(fieldMeta);

            if (fieldCodec != null) {
                codecMap.putIfAbsent(fieldMeta, fieldCodec);
            }

        }
        return Collections.unmodifiableMap(codecMap);
    }

}
