package io.army.criteria.impl;

import io.army.criteria.SQLModifier;

enum MySQLLock implements SQLModifier {

    FOR_UPDATE("FOR UPDATE"),
    LOCK_IN_SHARE_MODE("LOCK IN SHARE MODE");

    final String words;

    MySQLLock(String words) {
        this.words = words;
    }

    @Override
    public final String render() {
        return this.words;
    }


}
