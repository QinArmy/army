package io.army.criteria.impl;

import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.mysql.MySQLQuery;
import io.army.lang.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @since 1.0
 */
abstract class MySQLNestedItems<C, FT, FS, FP, IR, IC, JT, JS, JP> extends JoinableClause<C, FT, FS, FP, JT, JS, JP>
        implements MySQLQuery._IndexHintClause<C, IR, IC>, MySQLQuery._IndexPurposeClause<C, IC> {

    private MySQLNestedItems(Consumer<_TableBlock> blockConsumer, @Nullable C criteria) {
        super(blockConsumer, criteria);
    }


    @Override
    public final IR useIndex() {
        return null;
    }

    @Override
    public final IR ignoreIndex() {
        return null;
    }

    @Override
    public final IR forceIndex() {
        return null;
    }

    @Override
    public final IR ifUseIndex(Predicate<C> predicate) {
        return null;
    }

    @Override
    public final IR ifIgnoreIndex(Predicate<C> predicate) {
        return null;
    }

    @Override
    public final IR ifForceIndex(Predicate<C> predicate) {
        return null;
    }

    @Override
    public final IC useIndex(List<String> indexList) {
        return null;
    }

    @Override
    public final IC ignoreIndex(List<String> indexList) {
        return null;
    }

    @Override
    public final IC forceIndex(List<String> indexList) {
        return null;
    }

    @Override
    public final IC ifUseIndex(Function<C, List<String>> function) {
        return null;
    }

    @Override
    public final IC ifIgnoreIndex(Function<C, List<String>> function) {
        return null;
    }

    @Override
    public final IC ifForceIndex(Function<C, List<String>> function) {
        return null;
    }


    @Override
    public final IC forOrderBy(List<String> indexList) {
        return null;
    }

    @Override
    public final IC forOrderBy(Function<C, List<String>> function) {
        return null;
    }

    @Override
    public final IC forJoin(List<String> indexList) {
        return null;
    }

    @Override
    public final IC forJoin(Function<C, List<String>> function) {
        return null;
    }

    @Override
    public final IC forGroupBy(List<String> indexList) {
        return null;
    }

    @Override
    public final IC forGroupBy(Function<C, List<String>> function) {
        return null;
    }


}
