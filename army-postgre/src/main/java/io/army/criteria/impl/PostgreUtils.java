package io.army.criteria.impl;

abstract class PostgreUtils extends CriteriaUtils {

    private PostgreUtils() {
    }


    static int selectModifier(final PostgreSyntax.Modifier modifier) {
        final int level;
        if (modifier == Postgres.ALL
                || modifier == Postgres.DISTINCT) {
            level = 0;
        } else {
            level = -1;
        }
        return level;
    }


}
