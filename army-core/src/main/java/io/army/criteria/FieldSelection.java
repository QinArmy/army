package io.army.criteria;

import io.army.meta.FieldMeta;

public interface FieldSelection extends Selection {

    FieldMeta<?, ?> fieldMeta();

}
