package io.army.criteria.mysql;

import io.army.criteria.SQLModifier;
import io.army.meta.IndexMeta;

import java.util.List;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
 */
public interface MySQL57IndexHint {

    /**
     * @return {@code {USE|IGNORE|FORCE} }
     */
    SQLModifier command();

    /**
     * @return {@code FOR {JOIN|ORDER BY|GROUP BY} }
     */
    SQLModifier scope();

    /**
     * @return a unmodifiable list
     */
    List<IndexMeta<?>> indexMetaList();
}
