package io.army.schema;

import io.army.meta.FieldMeta;

final class FieldResultBuilder {

    FieldResultBuilder field(FieldMeta<?, ?> field) {
        return this;
    }

    FieldResultBuilder sqlType(boolean sqlType) {
        return this;
    }

    FieldResultBuilder defaultExp(boolean defaultExp) {
        return this;
    }

    FieldResultBuilder nullable(boolean nullable) {
        return this;
    }

    void comment(boolean comment) {

    }


    _FieldResult buildAndClear() {
        throw new UnsupportedOperationException();
    }

}
