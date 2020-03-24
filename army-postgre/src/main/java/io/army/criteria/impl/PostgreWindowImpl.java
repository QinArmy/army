package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLContext;
import io.army.criteria.SQLModifier;
import io.army.criteria.postgre.PostgreSelect;
import io.army.dialect.TableDML;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

final class PostgreWindowImpl<C> implements PostgreSelect.PostgreWindow, PostgreSelect.PostgreWindowNameAble<C> {

    private final C criteria;

    private String windowName;

    private String existingWindowName;

    private List<Expression<?>> partitionExpList;

    private List<Expression<?>> orderExpList;

    private SQLModifier frameOption;

    private String rangeText;

    private SQLModifier exclude;

    PostgreWindowImpl(C criteria) {
        this.criteria = criteria;
    }


    @Override
    public String windowName() {
        return this.windowName;
    }

    @Override
    public void appendSQL(SQLContext context) {
        Assert.state(StringUtils.hasText(this.windowName), "windowName is null,state error.");
        TableDML dml = context.dml();
        boolean hasList = false;
        //1. below window_name clause
        StringBuilder builder = context.stringBuilder()
                .append(" ")
                .append(dml.quoteIfNeed(this.windowName))
                .append(" AS (");

        //2. below existing_window_name clause

        if (StringUtils.hasText(this.existingWindowName)) {
            builder.append(" ")
                    .append(dml.quoteIfNeed(this.existingWindowName));
        }
        //3. below  PARTITION BY clause

        if (!CollectionUtils.isEmpty(this.partitionExpList)) {
            hasList = true;
            builder.append(" PARTITION BY ");
            for (Iterator<Expression<?>> iterator = this.partitionExpList.iterator(); iterator.hasNext(); ) {
                iterator.next().appendSQL(context);
                if (iterator.hasNext()) {
                    builder.append(",");
                }
            }
        }

        //4. below order by clause
        if (!CollectionUtils.isEmpty(this.orderExpList)) {
            hasList = true;
            builder.append(" ORDER BY ");
            for (Iterator<Expression<?>> iterator = this.orderExpList.iterator(); iterator.hasNext(); ) {
                iterator.next().appendSQL(context);
                if (iterator.hasNext()) {
                    builder.append(",");
                }
            }
        }

        if (this.frameOption == null) {
            Assert.state(hasList, "window clause build error.");
            builder.append(" )");
            return;
        }

        //5. below frame clause
        builder.append(" ")
                .append(frameOption.render())
                .append(" ")
                .append(this.rangeText);

        if (exclude != null) {
            builder.append(" ")
                    .append(this.exclude.render())
            ;
        }

        builder.append(" )");

    }

    @Override
    public PostgreSelect.PostgreWindow asWindow() {
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowRefAble<C> windowName(String name) {
        Assert.hasText(name, "window name required");
        this.windowName = name;
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowPartitionByAble<C> ref(String existingWindowName) {
        Assert.hasText(existingWindowName, "existingWindowName required");
        this.existingWindowName = existingWindowName;
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowPartitionByAble<C> ifRef(Predicate<C> predicate, String existingWindowName) {
        if (predicate.test(this.criteria)) {
            Assert.hasText(existingWindowName, "existingWindowName required");
            this.existingWindowName = existingWindowName;
        }
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowOrderByAble<C> partitionBy(Expression<?> partitionExp) {
        getOrBuildPartitionExpList()
                .add(partitionExp);
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowOrderByAble<C> partitionBy(Function<C, List<Expression<?>>> function) {
        getOrBuildPartitionExpList()
                .addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowOrderByAble<C> ifPartitionBy(Predicate<C> predicate
            , Expression<?> partitionExp) {
        if (predicate.test(this.criteria)) {
            getOrBuildPartitionExpList()
                    .add(partitionExp);
        }
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowOrderByAble<C> ifPartitionBy(Predicate<C> predicate
            , Function<C, List<Expression<?>>> function) {
        if (predicate.test(this.criteria)) {
            getOrBuildPartitionExpList()
                    .addAll(function.apply(this.criteria));
        }
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameAble<C> orderBy(Expression<?> expression) {
        getOrBuildOrderExpList()
                .add(expression);
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameAble<C> orderBy(Function<C, List<Expression<?>>> function) {
        getOrBuildOrderExpList()
                .addAll(function.apply(this.criteria));
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> expression) {
        if (predicate.test(this.criteria)) {
            getOrBuildOrderExpList()
                    .add(expression);
        }
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameAble<C> ifOrderBy(Predicate<C> predicate
            , Function<C, List<Expression<?>>> function) {
        if (predicate.test(this.criteria)) {
            getOrBuildOrderExpList()
                    .addAll(function.apply(this.criteria));
        }
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameStartEndAble<C> range() {
        this.frameOption = FrameOption.RANGE;
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameStartEndAble<C> rows() {
        this.frameOption = FrameOption.ROWS;
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameStartEndAble<C> groups() {
        this.frameOption = FrameOption.GROUPS;
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameExclusion startPreceding(Long offset) {
        this.rangeText = Range.offsetPreceding(offset);
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameExclusion startFollowing(Long offset) {
        this.rangeText = Range.offsetFollowing(offset);
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameExclusion startPreceding() {
        this.rangeText = Range.UNBOUNDED_PRECEDING.keyWords;
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameExclusion startCurrentRow() {
        this.rangeText = Range.CURRENT_ROW.keyWords;
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameExclusion startFollowing() {
        this.rangeText = Range.UNBOUNDED_FOLLOWING.keyWords;
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameExclusion between(Long startOffset, Long endOffset) {
        this.rangeText = Range.between(startOffset, endOffset);
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameExclusion betweenPreceding(Long endOffset) {
        this.rangeText = Range.between(Range.UNBOUNDED_PRECEDING, endOffset);
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameExclusion betweenFollowing(Long startOffset) {
        this.rangeText = Range.between(startOffset, Range.UNBOUNDED_FOLLOWING);
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameExclusion betweenPrecedingAndFollowing() {
        this.rangeText = Range.between(Range.UNBOUNDED_PRECEDING, Range.UNBOUNDED_FOLLOWING);
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameExclusion betweenPrecedingAndCurrentRow() {
        this.rangeText = Range.between(Range.UNBOUNDED_PRECEDING, Range.CURRENT_ROW);
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowFrameExclusion betweenCurrentRowAndFollowing() {
        this.rangeText = Range.between(Range.CURRENT_ROW, Range.UNBOUNDED_FOLLOWING);
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowClauseAble excludeCurrentRow() {
        this.exclude = Exclude.EXCLUDE_CURRENT_ROW;
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowClauseAble excludeGroup() {
        this.exclude = Exclude.EXCLUDE_GROUP;
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowClauseAble excludeTies() {
        this.exclude = Exclude.EXCLUDE_TIES;
        return this;
    }

    @Override
    public PostgreSelect.PostgreWindowClauseAble excludeNoOthers() {
        this.exclude = Exclude.EXCLUDE_NO_OTHERS;
        return this;
    }

    /*################################## blow private method ##################################*/

    private List<Expression<?>> getOrBuildPartitionExpList() {
        if (this.partitionExpList == null) {
            this.partitionExpList = new ArrayList<>(3);
        }
        return this.partitionExpList;
    }

    private List<Expression<?>> getOrBuildOrderExpList() {
        if (this.orderExpList == null) {
            this.orderExpList = new ArrayList<>(3);
        }
        return this.orderExpList;
    }

    /*################################## blow static inner class  ##################################*/

    private enum FrameOption implements SQLModifier {

        RANGE,
        ROWS,
        GROUPS;

        @Override
        public String render() {
            return this.name();
        }
    }

    private enum Exclude implements SQLModifier {

        EXCLUDE_CURRENT_ROW("EXCLUDE CURRENT ROW"),
        EXCLUDE_GROUP("EXCLUDE GROUP"),
        EXCLUDE_TIES("EXCLUDE TIES"),
        EXCLUDE_NO_OTHERS("EXCLUDE NO OTHERS");

        private final String keyWords;

        Exclude(String keyWords) {
            this.keyWords = keyWords;
        }

        @Override
        public String render() {
            return this.keyWords;
        }
    }

    private enum Range implements SQLModifier {

        UNBOUNDED_PRECEDING("UNBOUNDED PRECEDING"),
        CURRENT_ROW("CURRENT ROW"),
        UNBOUNDED_FOLLOWING("UNBOUNDED FOLLOWING"),
        PRECEDING("PRECEDING"),
        FOLLOWING("FOLLOWING");

        private final String keyWords;

        Range(String keyWords) {
            this.keyWords = keyWords;
        }

        @Override
        public String render() {
            return this.keyWords;
        }

        static String offsetPreceding(Long start) {
            return start + " " + PRECEDING.keyWords;
        }

        static String offsetFollowing(Long start) {
            return start + " " + FOLLOWING.keyWords;
        }

        static String between(Long start, Long end) {
            return "BETWEEN " + offsetPreceding(start) + " AND " + offsetFollowing(end);
        }

        static String between(Long start, Range end) {
            return "BETWEEN " + offsetPreceding(start) + " AND " + end.keyWords;
        }

        static String between(Range start, Long end) {
            return "BETWEEN " + start.keyWords + " AND " + offsetFollowing(end);
        }

        static String between(Range start, Range end) {
            return "BETWEEN " + start.keyWords + " AND " + end.keyWords;
        }
    }

}
