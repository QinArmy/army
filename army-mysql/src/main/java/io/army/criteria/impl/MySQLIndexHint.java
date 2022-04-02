package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.SQLModifier;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.lang.Nullable;
import io.army.util._CollectionUtils;

import java.util.List;

final class MySQLIndexHint implements _IndexHint {

    private final Command command;

    private final Purpose purpose;

    private final List<String> indexNameList;

    MySQLIndexHint(Command command, @Nullable Purpose purpose, List<String> indexNameList) {
        if (indexNameList.size() == 0) {
            throw new CriteriaException("index hint index name list must not empty.");
        }
        this.command = command;
        this.purpose = purpose;
        this.indexNameList = _CollectionUtils.asUnmodifiableList(indexNameList);
    }


    @Override
    public SQLModifier command() {
        return this.command;
    }

    @Override
    public SQLModifier purpose() {
        return this.purpose;
    }

    @Override
    public List<String> indexNameList() {
        return this.indexNameList;
    }


    enum Command implements SQLModifier {

        USER_INDEX(" USER INDEX"),
        IGNORE_INDEX(" IGNORE INDEX"),
        FORCE_INDEX(" FORCE INDEX");

        private final String words;

        Command(String words) {
            this.words = words;
        }

        @Override
        public final String render() {
            return this.words;
        }

        @Override
        public final String toString() {
            return String.format("%s.%s", Command.class.getName(), this.name());
        }


    }//Command

    enum Purpose implements SQLModifier {

        FOR_ORDER_BY(" FOR ORDER BY"),
        FOR_GROUP_BY(" FOR GROUP BY"),
        FOR_JOIN(" FOR JOIN");

        private final String words;

        Purpose(String words) {
            this.words = words;
        }

        @Override
        public final String render() {
            return this.words;
        }


        @Override
        public final String toString() {
            return String.format("%s.%s", Purpose.class.getName(), this.name());
        }

    }// Purpose


}
