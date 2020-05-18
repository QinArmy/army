package io.army.modelgen;

import io.army.annotation.Index;

enum IndexMode {

    GENERIC,
    UNIQUE,
    PRIMARY;

    static IndexMode resolve(Index index) {
        IndexMode mode;
        if (index.unique() && index.columnList().length == 1) {
            mode = IndexMode.UNIQUE;
        } else {
            mode = IndexMode.GENERIC;
        }
        return mode;
    }
}
