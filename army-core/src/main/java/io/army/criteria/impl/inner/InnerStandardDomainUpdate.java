package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.meta.TableMeta;

import java.util.List;

@DeveloperForbid
public interface InnerStandardDomainUpdate extends InnerUpdate {

    TableMeta<?> tableMata();

    String tableAlias();

    Object primaryKeyValue();

    List<IPredicate> predicateList();
}
