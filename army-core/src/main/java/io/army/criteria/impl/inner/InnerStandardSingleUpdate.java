package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.meta.TableMeta;

import java.util.List;

@DeveloperForbid
public interface InnerStandardSingleUpdate extends InnerUpdate {

    TableMeta<?> tableMeta();

    String tableAlias();


    List<IPredicate> predicateList();
}
