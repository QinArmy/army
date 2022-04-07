package io.army.criteria.impl;

/**
 * <p>
 * SubQuery hint strategy
 * </p>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-subquery">Subquery Optimizer Hints</a>
 */
public enum HintStrategy {
    DUPSWEEDOUT,
    FIRSTMATCH,
    LOOSESCAN,
    MATERIALIZATION
}
