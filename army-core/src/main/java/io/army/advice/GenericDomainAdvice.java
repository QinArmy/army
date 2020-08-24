package io.army.advice;

import io.army.meta.TableMeta;

import java.util.Set;

public interface GenericDomainAdvice {

    int order();

    Set<TableMeta<?>> supportTableMetaSet();
}
