package io.army.boot.sync;

import io.army.ErrorCode;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.CriteriaException;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class AbstractSyncApiSession extends AbstractGenericSyncApiSession {


    static Object obtainRouteKeyValueForInsert(TableMeta<?> tableMeta
            , ReadonlyWrapper wrapper, boolean dataSourceRoute) {

        List<FieldMeta<?, ?>> routeFieldList;
        if (dataSourceRoute) {
            routeFieldList = tableMeta.dataSourceRouteField();
        } else {
            routeFieldList = tableMeta.tableRouteField();
        }
        final int size = routeFieldList.size();

        Object routeKeyValue;
        if (size == 1) {
            routeKeyValue = wrapper.getPropertyValue(routeFieldList.get(0).propertyName());
            if (routeKeyValue == null) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "TableMeta [%s] data source route field required.", tableMeta);
            }
        } else if (size > 1) {
            List<Object> routeKeyValueList = new ArrayList<>(size);
            for (FieldMeta<?, ?> fieldMeta : routeFieldList) {
                Object fieldValue = wrapper.getPropertyValue(fieldMeta.propertyName());
                if (fieldValue != null) {
                    routeKeyValueList.add(fieldValue);
                }
            }
            if (routeKeyValueList.isEmpty()) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "TableMeta [%s] data source route field required.", tableMeta);
            }
            routeKeyValue = Collections.unmodifiableList(routeKeyValueList);
        } else {
            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "TableMeta[%s] not support data source route."
                    , tableMeta);
        }
        return routeKeyValue;
    }
}
