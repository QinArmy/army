package io.army.session;

import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.criteria.Selection;
import io.army.criteria.TypeInfer;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.meta.TypeMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.List;
import java.util.Map;

public abstract class ExecutorSupport {

    protected ExecutorSupport() {
    }


    protected static int restSeconds(final int timeout, final long startTime) {
        if (timeout == 0) {
            return 0;
        }
        if (timeout < 0) {
            //no bug,never here
            throw new IllegalArgumentException();
        }
        int restSec;
        final long restMills;
        restMills = (timeout * 1000L) - (System.currentTimeMillis() - startTime);
        if (restMills < 1) {
            throw _Exceptions.timeout(timeout, restMills);
        }
        restSec = (int) (restMills / 1000L);
        if (restMills % 1000L != 0) {
            restSec++;
        }
        return restSec;
    }


    protected static MappingType compatibleTypeFrom(final TypeInfer infer, final @Nullable Class<?> resultClass,
                                                    final ObjectAccessor accessor, final String fieldName)
            throws NoMatchMappingException {
        final MappingType type;
        if (infer instanceof MappingType) {
            type = (MappingType) infer;
        } else if (infer instanceof TypeMeta) {
            type = ((TypeMeta) infer).mappingType();
        } else {
            type = infer.typeMeta().mappingType();
        }
        MappingType compatibleType;
        if (accessor == ObjectAccessorFactory.PSEUDO_ACCESSOR) {
            assert resultClass != null;
            if (resultClass.isAssignableFrom(type.javaType())) {
                compatibleType = type;
            } else {
                compatibleType = type.compatibleFor(resultClass);
            }
        } else if (accessor.isWritable(fieldName, type.javaType())) {
            compatibleType = type;
        } else {
            compatibleType = type.compatibleFor(accessor.getJavaType(fieldName));
        }
        return compatibleType;
    }


    /**
     * @return a unmodified map
     */
    protected static Map<String, Integer> createAliasToIndexMap(final List<? extends Selection> selectionList) {
        final int selectionSize = selectionList.size();
        Map<String, Integer> map = _Collections.hashMap((int) (selectionSize / 0.75f));
        for (int i = 0; i < selectionSize; i++) {
            map.put(selectionList.get(i).label(), i); // If alias duplication,then override.
        }
        return _Collections.unmodifiableMap(map);
    }


}
