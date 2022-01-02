package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.SQLModifier;
import io.army.criteria.TablePart;
import io.army.criteria.mysql.MySQLQuery;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

abstract class MySQLIndexHintOnClause<C, OR, IR, WP, WR> implements MySQLQuery.IndexHintClause<C, IR>, MySQLQuery.IndexHintWordClause<WP, WR>
        , MySQLQuery.IndexHintPurposeClause<WR> {

    private List<SQLModifier> indexHints;

    private List<String> indexNames;

    MySQLIndexHintOnClause(TablePart tablePart, JoinType joinType, OR query) {

    }


    @Override
    public final IR useIndex() {
        return createIndexHint(IndexHint.USE);
    }

    @Override
    public final IR ignoreIndex() {
        return createIndexHint(IndexHint.IGNORE);
    }

    @Override
    public final IR forceIndex() {
        return createIndexHint(IndexHint.FORCE);
    }

    @Override
    public final IR ifUseIndex(Predicate<C> predicate) {
        if (predicate.test(this.getCriteria())) {
            createIndexHint(IndexHint.USE);
        }
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndex(Predicate<C> predicate) {
        if (predicate.test(this.getCriteria())) {
            createIndexHint(IndexHint.IGNORE);
        }
        return (IR) this;
    }

    @Override
    public final IR ifForceIndex(Predicate<C> predicate) {
        if (predicate.test(this.getCriteria())) {
            createIndexHint(IndexHint.FORCE);
        }
        return (IR) this;
    }

    @Override
    public final WP index() {
        addIndexWordIfNeed(IndexHint.INDEX);
        return (WP) this;
    }


    @Override
    public final WP key() {
        addIndexWordIfNeed(IndexHint.KEY);
        return (WP) this;
    }

    @Override
    public final WR index(final List<String> indexNameList) {
        if (addIndexWordIfNeed(IndexHint.INDEX)) {
            if (!CollectionUtils.isEmpty(indexNameList)) {
                throw new CriteriaException("index list is empty.");
            }
            this.indexHints = Collections.unmodifiableList(this.indexHints);
            this.indexNames = Collections.unmodifiableList(new ArrayList<>(indexNameList));
        }
        return (WR) this;
    }

    @Override
    public final WR key(final List<String> indexNameList) {
        if (addIndexWordIfNeed(IndexHint.KEY)) {
            if (!CollectionUtils.isEmpty(indexNameList)) {
                throw new CriteriaException("index list is empty.");
            }
            this.indexHints = Collections.unmodifiableList(this.indexHints);
            this.indexNames = Collections.unmodifiableList(new ArrayList<>(indexNameList));
        }
        return (WR) this;
    }

    @Override
    public final WR forOrderBy(List<String> indexNameList) {
        return addPurposeIndexListIfNeed(IndexHint.FOR_ORDER_BY, indexNameList);
    }

    @Override
    public final WR forJoin(List<String> indexNameList) {
        return addPurposeIndexListIfNeed(IndexHint.FOR_JOIN, indexNameList);
    }

    @Override
    public final WR forGroupBy(List<String> indexNameList) {
        return addPurposeIndexListIfNeed(IndexHint.FOR_GROUP_BY, indexNameList);
    }

    @Override
    public final List<SQLModifier> indexHintList() {
        List<SQLModifier> indexHints = this.indexHints;
        if (indexHints == null) {
            indexHints = Collections.emptyList();
        }
        return indexHints;
    }

    @Override
    public final List<String> indexNameList() {
        List<String> indexNames = this.indexNames;
        if (indexNames == null) {
            indexNames = Collections.emptyList();
        }
        return indexNames;
    }

    private IR createIndexHint(final IndexHint hint) {
        if (!(this.tablePart instanceof TableMeta)) {
            throw _Exceptions.castCriteriaApi();
        }
        switch (hint) {
            case USE:
            case IGNORE:
            case FORCE:
                break;
            default:
                throw _Exceptions.unexpectedEnum(hint);
        }
        final List<SQLModifier> indexHints = new ArrayList<>(3);
        indexHints.add(IndexHint.USE);
        this.indexHints = indexHints;
        return (IR) this;
    }

    private boolean addIndexWordIfNeed(final IndexHint hintWord) {
        final List<SQLModifier> indexHints = this.indexHints;
        final boolean added;
        if (indexHints != null && indexHints.size() == 1) {
            final IndexHint command = (IndexHint) indexHints.get(0);
            switch (command) {
                case USE:
                case IGNORE:
                case FORCE:
                    switch (hintWord) {
                        case INDEX:
                        case KEY:
                            indexHints.add(hintWord);
                            added = true;
                            break;
                        default:
                            throw _Exceptions.unexpectedEnum(hintWord);
                    }
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(command);
            }
        } else {
            added = false;
        }
        return added;
    }

    private WR addPurposeIndexListIfNeed(final IndexHint hint, final List<String> indexNameList) {
        switch (hint) {
            case FOR_ORDER_BY:
            case FOR_JOIN:
            case FOR_GROUP_BY:
                break;
            default:
                throw _Exceptions.unexpectedEnum(hint);
        }
        final List<SQLModifier> indexHints = this.indexHints;
        if (indexHints == null || indexHints.size() != 2) {
            return (WR) this;
        }
        final IndexHint command = (IndexHint) indexHints.get(0);
        switch (command) {
            case USE:
            case IGNORE:
            case FORCE:
                break;
            default:
                throw _Exceptions.unexpectedEnum(command);
        }
        final IndexHint word = (IndexHint) indexHints.get(1);
        switch (word) {
            case INDEX:
            case KEY:
                break;
            default:
                throw _Exceptions.unexpectedEnum(word);
        }
        if (!CollectionUtils.isEmpty(indexNameList)) {
            throw new CriteriaException("index list is empty.");
        }
        indexHints.add(hint);
        this.indexHints = Collections.unmodifiableList(indexHints);
        this.indexNames = Collections.unmodifiableList(new ArrayList<>(indexNameList));
        return (WR) this;
    }

    private enum IndexHint implements SQLModifier {

        USE("USE"),
        IGNORE("IGNORE"),
        FORCE("FORCE"),
        INDEX("INDEX"),
        KEY("KEY"),
        FOR_ORDER_BY("FOR ORDER BY"),
        FOR_GROUP_BY("FOR GROUP BY"),
        FOR_JOIN("FOR JOIN");

        private final String words;

        IndexHint(String words) {
            this.words = words;
        }

        @Override
        public final String render() {
            return this.words;
        }

    }


    static abstract class NoPartitionBlock<C, OR, IR, WP, WR> extends MySQLIndexHintOnClause<C, OR, IR, WP, WR> {

        final String alias;

        NoPartitionBlock(TablePart tablePart, String alias, JoinType joinType, OR query) {
            super(tablePart, joinType, query);
            this.alias = alias;
        }

        @Override
        public final String alias() {
            return this.alias;
        }

        @Override
        public final List<String> partitionList() {
            return Collections.emptyList();
        }

    }//NoPartitionBlock


}
