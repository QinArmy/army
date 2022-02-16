package io.army.modelgen;

import io.army.annotation.Index;

enum IndexMode {

    GENERIC,
    UNIQUE,
    PRIMARY;

    static IndexMode resolve(final Index index) {
        final IndexMode mode;
        if (index.unique()) {
            final String[] columnList = index.fieldList();
            if (columnList.length == 1) {
                mode = _MetaBridge.ID.equals(columnList[0]) ? IndexMode.PRIMARY : IndexMode.UNIQUE;
            } else {
                mode = IndexMode.GENERIC;
            }
        } else {
            mode = IndexMode.GENERIC;
        }
        return mode;
    }


}
