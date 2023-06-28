package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner.*;
import io.army.criteria.standard.StandardDelete;
import io.army.criteria.standard.StandardInsert;
import io.army.criteria.standard.StandardQuery;
import io.army.criteria.standard.StandardUpdate;
import io.army.dialect._Constant;
import io.army.dialect._SetClauseContext;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyBuildInMapping;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.modelgen._MetaBridge;
import io.army.sqltype.MySQLType;
import io.army.sqltype.SqlType;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static io.army.dialect.Database.H2;
import static io.army.dialect.Database.PostgreSQL;

/**
 * <p>
 * This class is util class used to create standard sql statement.
 * </p>
 */
@SuppressWarnings({"unused"})
public abstract class SQLs extends SQLsSyntax {


    /**
     * private constructor
     */
    private SQLs() {
    }


    public static final WordAll ALL = SqlWords.KeyWordAll.ALL;

    public static final WordDistinct DISTINCT = SqlWords.KeyWordDistinct.DISTINCT;
    public static final WordLateral LATERAL = SqlWords.KeyWordLateral.LATERAL;
    public static final WordFirst FIRST = SqlWords.KeyWordFirst.FIRST;
    public static final WordNext NEXT = SqlWords.KeyWordNext.NEXT;
    public static final WordPercent PERCENT = SqlWords.KeyWordPercent.PERCENT;
    public static final Statement.NullsFirstLast NULLS_FIRST = SqlWords.KeyWordsNullsFirstLast.NULLS_FIRST;
    public static final Statement.NullsFirstLast NULLS_LAST = SqlWords.KeyWordsNullsFirstLast.NULLS_LAST;
    public static final WordOnly ONLY = SqlWords.KeyWordOny.ONLY;
    public static final WordRow ROW = SqlWords.KeyWordRow.ROW;
    public static final WordRows ROWS = SqlWords.KeyWordRows.ROWS;
    public static final WordInterval INTERVAL = SqlWords.KeyWordInterval.INTERVAL;
    public static final WordsWithTies WITH_TIES = SqlWords.KeyWordWithTies.WITH_TIES;
    public static final BooleanTestWord UNKNOWN = SqlWords.KeyWordUnknown.UNKNOWN;
    /**
     * package field
     */
    static final Statement.AscDesc ASC = SqlWords.KeyWordAscDesc.ASC;
    /**
     * package field
     */
    static final Statement.AscDesc DESC = SqlWords.KeyWordAscDesc.DESC;


    public static final WordAs AS = SqlWords.KeyWordAs.AS;

    public static final WordAnd AND = SqlWords.KeyWordAnd.AND;


    public static final TrimPosition BOTH = SqlWords.WordTrimPosition.BOTH;
    public static final TrimPosition LEADING = SqlWords.WordTrimPosition.LEADING;
    public static final TrimPosition TRAILING = SqlWords.WordTrimPosition.TRAILING;
    public static final WordIn IN = SqlWords.KeyWordIn.IN;
    public static final WordFor FOR = SqlWords.KeyWordFor.FOR;
    public static final WordFrom FROM = SqlWords.KeyWordFrom.FROM;
    public static final WordSimilar SIMILAR = SqlWords.KeyWordSimilar.SIMILAR;

    public static final WordOn ON = SqlWords.KeyWordOn.ON;

    public static final Window.RowModifier UNBOUNDED_PRECEDING = SQLWindow.WindowRowModifier.UNBOUNDED_PRECEDING;

    public static final Window.RowModifier CURRENT_ROW = SQLWindow.WindowRowModifier.CURRENT_ROW;

    public static final Window.RowModifier UNBOUNDED_FOLLOWING = SQLWindow.WindowRowModifier.UNBOUNDED_FOLLOWING;

    public static final Window.ExpModifier PRECEDING = SQLWindow.WindowExpModifier.PRECEDING;

    public static final Window.ExpModifier FOLLOWING = SQLWindow.WindowExpModifier.FOLLOWING;


    @Support({PostgreSQL, H2})
    public static final BetweenModifier SYMMETRIC = SqlWords.KeyWordSymmetric.SYMMETRIC;

    @Support({H2})
    public static final BetweenModifier ASYMMETRIC = SqlWords.KeyWordSymmetric.ASYMMETRIC;

    public static final SymbolAsterisk ASTERISK = SqlWords.SQLSymbolAsterisk.ASTERISK;

    public static final SymbolPeriod PERIOD = SqlWords.SQLSymbolPeriod.PERIOD;

    public static final SymbolSpace SPACE = SqlWords.SymbolSpaceEnum.SPACE;

    public static final SymbolEqual EQUAL = SqlWords.SymbolEqualEnum.EQUAL;

    public static final WordBooleans TRUE = OperationPredicate.booleanWord(true);

    public static final WordBooleans FALSE = OperationPredicate.booleanWord(false);

    public static final WordDefault DEFAULT = new DefaultWord();

    public static final WordNull NULL = OperationExpression.nullWord();


    public static final QuantifiedWord SOME = SqlWords.QueryOperator.SOME;
    public static final QuantifiedWord ANY = SqlWords.QueryOperator.ANY;

    public static final IsComparisonWord DISTINCT_FROM = SqlWords.IsComparisonKeyWord.DISTINCT_FROM;

    public static final WordEscape ESCAPE = SqlWords.KeyWordEscape.ESCAPE;


    /**
     * package field
     */
    static final Expression _ASTERISK_EXP = new LiteralSymbolAsterisk();


    static UnaryOperator<Item> ERROR_FUNC = SQLs::castCriteria;

    static final UnaryOperator<Select> SIMPLE_SELECT = SQLs::identity;

    static final UnaryOperator<SubQuery> SUB_QUERY = SQLs::identity;


    public static StandardInsert._PrimaryOptionSpec<Insert> singleInsert() {
        return StandardInserts.singleInsert();
    }

    public static StandardInsert._PrimaryOption20Spec<Insert> singleInsert20() {
        return StandardInserts.singleInsert20();
    }


    public static StandardUpdate._DomainUpdateClause<Update> domainUpdate() {
        return StandardUpdates.simpleDomain();
    }


    public static StandardUpdate._SingleUpdateClause<Update> singleUpdate() {
        return StandardUpdates.singleUpdate(StandardDialect.STANDARD10);
    }

    public static StandardUpdate._WithSpec<Update> singleUpdate20() {
        return StandardUpdates.singleUpdate(StandardDialect.STANDARD20);
    }


    /**
     * <p>
     * Batch domain update
     * </p>
     */
    public static StandardUpdate._DomainUpdateClause<Statement._BatchUpdateParamSpec> batchDomainUpdate() {
        return StandardUpdates.batchDomain();
    }


    /**
     * <p>
     * Batch domain update
     * </p>
     */
    public static StandardUpdate._SingleUpdateClause<Statement._BatchUpdateParamSpec> batchSingleUpdate() {
        return StandardUpdates.batchSingleUpdate(StandardDialect.STANDARD10);
    }

    public static StandardUpdate._WithSpec<Statement._BatchUpdateParamSpec> batchSingleUpdate20() {
        return StandardUpdates.batchSingleUpdate(StandardDialect.STANDARD20);
    }


    public static StandardDelete._StandardDeleteClause<Delete> singleDelete() {
        return StandardDeletes.singleDelete(StandardDialect.STANDARD10);
    }

    public static StandardDelete._WithSpec<Delete> singleDelete20() {
        return StandardDeletes.singleDelete(StandardDialect.STANDARD20);
    }


    public static StandardDelete._DomainDeleteClause<Delete> domainDelete() {
        return StandardDeletes.domainDelete();
    }

    /**
     * <p>
     * Batch domain delete
     * </p>
     */
    public static StandardDelete._StandardDeleteClause<Statement._BatchDeleteParamSpec> batchSingleDelete() {
        return StandardDeletes.batchSingleDelete(StandardDialect.STANDARD10);
    }

    /**
     * <p>
     * Batch domain delete
     * </p>
     */
    public static StandardDelete._WithSpec<Statement._BatchDeleteParamSpec> batchSingleDelete20() {
        return StandardDeletes.batchSingleDelete(StandardDialect.STANDARD20);
    }


    public static StandardDelete._DomainDeleteClause<Statement._BatchDeleteParamSpec> batchDomainDelete() {
        return StandardDeletes.batchDomainDelete();
    }


    public static StandardQuery._SelectSpec<Select> query() {
        return StandardQueries.simpleQuery(StandardDialect.STANDARD10);
    }

    public static StandardQuery._SelectSpec<Statement._BatchSelectParamSpec> batchQuery() {
        return StandardQueries.batchQuery(StandardDialect.STANDARD10);
    }

    public static StandardQuery._WithSpec<Select> query20() {
        return StandardQueries.simpleQuery(StandardDialect.STANDARD20);
    }

    public static StandardQuery._WithSpec<Statement._BatchSelectParamSpec> batchQuery20() {
        return StandardQueries.batchQuery(StandardDialect.STANDARD20);
    }

    public static StandardQuery._SelectSpec<SubQuery> subQuery() {
        return StandardQueries.subQuery(StandardDialect.STANDARD10, ContextStack.peek(), SUB_QUERY);
    }

    public static StandardQuery._WithSpec<SubQuery> subQuery20() {
        return StandardQueries.subQuery(StandardDialect.STANDARD20, ContextStack.peek(), SUB_QUERY);
    }


    public static StandardQuery._SelectSpec<Expression> scalarSubQuery() {
        return StandardQueries.subQuery(StandardDialect.STANDARD10, ContextStack.peek(), Expressions::scalarExpression);
    }

    public static StandardQuery._WithSpec<Expression> scalarSubQuery20() {
        return StandardQueries.subQuery(StandardDialect.STANDARD20, ContextStack.peek(), Expressions::scalarExpression);
    }



    /*-------------------below package method-------------------*/


    /**
     * <p>
     * package method that is used by army developer.
     * </p>
     *
     * @param value {@link Expression} or parameter.
     * @see #plusEqual(SQLField, Expression)
     */
    static SQLs.ArmyItemPair _itemPair(final @Nullable SQLField field, final @Nullable AssignOperator operator,
                                       final @Nullable Expression value) {
        if (field == null || value == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        //TODO right operand non-null validate
        final SQLs.ArmyItemPair itemPair;
        if (operator == null) {
            itemPair = new SQLs.FieldItemPair(field, (ArmyExpression) value);
        } else {
            itemPair = new SQLs.OperatorItemPair(field, operator, (ArmyExpression) value);
        }
        return itemPair;
    }

    /**
     * <p>
     * package method that is used by army developer.
     * </p>
     */
    static _ItemPair _itemExpPair(final SQLField field, @Nullable Expression value) {
        assert value != null;
        return SQLs._itemPair(field, null, value);
    }

    static ItemPair _itemPair(List<? extends SQLField> fieldList, SubQuery subQuery) {
        return new SQLs.RowItemPair(fieldList, subQuery);
    }


    /**
     * <p>
     * This method is similar to {@link Function#identity()}, except that use method reference.
     * </p>
     *
     * @see Function#identity()
     */
    static <T extends Item> T identity(T t) {
        return t;
    }


    static Item castCriteria(Item stmt) {
        throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
    }


    @Deprecated
    static BatchUpdate _batchUpdateIdentity(UpdateStatement update) {
        return (BatchUpdate) update;
    }

    static BatchDelete _batchDeleteIdentity(DeleteStatement delete) {
        return (BatchDelete) delete;
    }


    static <I extends Item> Function<TypeInfer, I> _toSelection(final Function<Selection, I> function) {
        return t -> {
            if (!(t instanceof Selection)) {
                throw ContextStack.castCriteriaApi(ContextStack.peek());
            }
            return function.apply((Selection) t);
        };
    }

    static <I extends Item> Function<TypeInfer, I> _ToExp(final Function<Expression, I> function) {
        return t -> {
            if (!(t instanceof Expression)) {
                throw ContextStack.castCriteriaApi(ContextStack.peek());
            }
            return function.apply((Expression) t);
        };
    }

    static <I extends Item> Function<TypeInfer, I> _ToPredicate(final Function<IPredicate, I> function) {
        return t -> {
            if (!(t instanceof IPredicate)) {
                throw ContextStack.castCriteriaApi(ContextStack.peek());
            }
            return function.apply((IPredicate) t);
        };
    }

    static SQLIdentifier _identifier(@Nullable String identifier) {
        if (identifier == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        return new SQLIdentifierImpl(identifier);
    }


    static String sqlKeyWordsToString(Enum<?> wordEnum) {
        return _StringUtils.builder()
                .append(SQLs.class.getSimpleName())
                .append(_Constant.PERIOD)
                .append(wordEnum.name())
                .toString();
    }


    public interface Modifier extends Query.SelectModifier {

    }

    public interface WordAll extends Modifier, QuantifiedWord {

    }

    public interface WordDistinct extends Modifier, ArgDistinct {

    }

    public interface WordOn {

    }

    public interface SymbolSpace {

    }

    public interface SymbolEqual {

    }

    public interface SymbolPeriod {

    }

    /**
     * @see SQLs#DISTINCT
     */
    public interface ArgDistinct extends SQLWords {

    }

    public interface SymbolAsterisk {

    }

    public interface WordNull extends BooleanTestWord, Expression, NullOption { // extends Expression not SimpleExpression

    }

    public interface WordAs extends SQLWords {

    }

    public interface WordDefault extends Expression {

    }

    public interface WordBooleans extends BooleanTestWord, SimplePredicate {

    }

    public interface QuantifiedWord extends SQLWords {

    }

    public interface WordAnd {

    }

    public interface WordEscape extends SQLWords {

    }

    public interface NullOption {

    }

    public interface BooleanTestWord extends SQLWords {

    }

    public interface DocumentValueOption extends SQLWords {

    }

    public interface WordDocument extends BooleanTestWord, DocumentValueOption {

    }

    public interface WordContent extends DocumentValueOption {

    }

    /**
     * package interface,this interface only is implemented by class or enum,couldn't is extended by interface.
     */
    interface ArmyKeyWord extends SQLWords {

    }

    public interface _ArrayConstructorSpec extends ArrayExpression {

        ArrayExpression castTo(MappingType type);

    }

    public interface IsComparisonWord extends SQLWords {

    }

    public interface BetweenModifier extends SQLWords {

    }

    public interface WordInterval extends SQLWords {

    }

    public interface WordPercent {

    }

    public interface WordOnly extends Query.TableModifier, Query.FetchOnlyWithTies, SQLWords {

    }

    public interface WordFirst extends Query.FetchFirstNext {

    }

    public interface WordNext extends Query.FetchFirstNext {

    }

    public interface WordRow extends Query.FetchRow {

    }

    public interface WordRows extends Query.FetchRow {

    }

    public interface WordLateral extends Query.DerivedModifier {

    }

    public interface WordsWithTies extends Query.FetchOnlyWithTies {

    }

    public interface WordPath extends SQLWords {

    }

    public interface WordsForOrdinality extends SQLWords {

    }

    public interface WordIn {

    }

    public interface WordFrom {

    }

    public interface WordFor {

    }

    public interface WordSimilar {

    }

    public interface TrimPosition {

    }




    /*-------------------below package method-------------------*/


    static abstract class ArmyItemPair implements _ItemPair {

        final RightOperand right;

        private ArmyItemPair(RightOperand right) {
            this.right = right;
        }
    }//ArmyItemPair

    /**
     * @see #_itemPair(SQLField, AssignOperator, Expression)
     */
    static class FieldItemPair extends ArmyItemPair implements _ItemPair._FieldItemPair {

        final SQLField field;

        private FieldItemPair(SQLField field, ArmyExpression value) {
            super(value);
            this.field = field;
        }

        @Override
        public final void appendItemPair(final StringBuilder sqlBuilder, final _SetClauseContext context) {
            final SQLField field = this.field;
            //1. append left item
            context.appendSetLeftItem(field);
            //2. append operator
            if (this instanceof OperatorItemPair) {
                ((OperatorItemPair) this).operator
                        .appendOperator(field, sqlBuilder, context);
            } else {
                sqlBuilder.append(_Constant.SPACE_EQUAL);
            }
            //3. append right item
            ((_Expression) this.right).appendSql(sqlBuilder, context);
        }

        @Override
        public final SQLField field() {
            return this.field;
        }

        @Override
        public final _Expression value() {
            return (_Expression) this.right;
        }

        @Override
        public final String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(this.field);
            if (this instanceof OperatorItemPair) {
                builder.append(((OperatorItemPair) this).operator);
            } else {
                builder.append(_Constant.SPACE_EQUAL);
            }
            builder.append(this.right);
            return builder.toString();
        }

    }//FieldItemPair

    private static final class OperatorItemPair extends FieldItemPair {

        final AssignOperator operator;

        private OperatorItemPair(SQLField field, AssignOperator operator, ArmyExpression value) {
            super(field, value);
            this.operator = operator;
        }


    }//OperatorItemPair

    static final class RowItemPair extends ArmyItemPair implements _ItemPair._RowItemPair {

        final List<SQLField> fieldList;

        private RowItemPair(List<? extends SQLField> fieldList, SubQuery subQuery) {
            super(subQuery);
            final int selectionCount;
            selectionCount = ((_RowSet) subQuery).selectionSize();
            if (fieldList.size() != selectionCount) {
                String m = String.format("Row column count[%s] and selection count[%s] of SubQuery not match."
                        , fieldList.size(), selectionCount);
                throw new CriteriaException(m);
            }
            final List<SQLField> tempList = _Collections.arrayList(fieldList.size());
            for (SQLField field : fieldList) {
                if (!(field instanceof TableField)) {
                    tempList.add(field);
                    continue;
                }
                if (((TableField) field).updateMode() == UpdateMode.IMMUTABLE) {
                    throw _Exceptions.immutableField(field);
                }
                final String fieldName = field.fieldName();
                if (_MetaBridge.UPDATE_TIME.equals(fieldName) || _MetaBridge.VERSION.equals(fieldName)) {
                    throw _Exceptions.armyManageField((TableField) field);
                }
                tempList.add(field);
            }
            this.fieldList = Collections.unmodifiableList(tempList);
        }

        @Override
        public void appendItemPair(final StringBuilder sqlBuilder, final _SetClauseContext context) {
            final List<? extends SQLField> fieldList = this.fieldList;
            final int fieldSize = fieldList.size();
            //1. append left paren
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            //2. append field list
            for (int i = 0; i < fieldSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                context.appendSetLeftItem(fieldList.get(i));
            }
            //3. append right paren
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

            //4. append '='
            sqlBuilder.append(_Constant.SPACE_EQUAL);

            //5. append sub query
            context.appendSubQuery((SubQuery) this.right);

        }

        @Override
        public List<? extends SQLField> rowFieldList() {
            return this.fieldList;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            //1. append left paren
            builder.append(_Constant.SPACE_LEFT_PAREN);
            final List<? extends SQLField> fieldList = this.fieldList;
            final int fieldSize = fieldList.size();
            //2. append field list
            for (int i = 0; i < fieldSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(fieldList.get(i));
            }
            //3. append right paren
            builder.append(_Constant.SPACE_RIGHT_PAREN);

            //4. append '='
            builder.append(_Constant.SPACE_EQUAL);

            //5. append sub query
            builder.append(this.right);
            return builder.toString();
        }

    }//RowItemPair


    static final class CteImpl implements _Cte {

        final String name;

        final List<String> columnNameList;

        final SubStatement subStatement;

        private final _SelectionMap selectionMap;

        CteImpl(String name, SubStatement subStatement) {
            this(name, Collections.emptyList(), subStatement);
        }

        /**
         * @param columnNameList unmodified list
         */
        CteImpl(String name, List<String> columnNameList, SubStatement subStatement) {
            this.name = name;
            this.columnNameList = columnNameList;
            this.subStatement = subStatement;

            if (!(subStatement instanceof DerivedTable)) {
                throw CriteriaUtils.subDmlNoReturningClause(name);
            } else if (this.columnNameList.size() == 0) {
                this.selectionMap = (_DerivedTable) subStatement;
            } else {
                this.selectionMap = CriteriaUtils.createAliasSelectionMap(this.columnNameList,
                        ((_DerivedTable) subStatement).refAllSelection(), name);
            }

        }

        @Override
        public String name() {
            return this.name;
        }


        @Override
        public List<String> columnAliasList() {
            return this.columnNameList;
        }

        @Override
        public SubStatement subStatement() {
            return this.subStatement;
        }

        @Override
        public List<? extends Selection> refAllSelection() {
            return this.selectionMap.refAllSelection();
        }


        @Override
        public Selection refSelection(final String name) {
            return this.selectionMap.refSelection(name);
        }


    }//CteImpl


    static final class SQLIdentifierImpl implements SQLIdentifier {

        private final String identifier;

        private SQLIdentifierImpl(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String render() {
            return this.identifier;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.identifier);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof SQLIdentifierImpl) {
                match = ((SQLIdentifierImpl) obj).identifier.equals(this.identifier);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return this.identifier;
        }


    }//SQLIdentifierImpl


    @Deprecated
    static final class _NullType extends _ArmyBuildInMapping {

        public static final _NullType INSTANCE = new _NullType();


        private _NullType() {
        }

        @Override
        public Class<?> javaType() {
            return Object.class;
        }

        @Override
        public SqlType map(final ServerMeta meta) {
            final SqlType sqlType;
            switch (meta.dialectDatabase()) {
                case MySQL:
                    sqlType = MySQLType.NULL;
                    break;
                case PostgreSQL:
                case Oracle:
                case H2:
                default:
                    throw MAP_ERROR_HANDLER.apply(this, meta);
            }
            return sqlType;
        }

        @Override
        public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
            return null;
        }

        @Override
        public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object afterGet(SqlType type, MappingEnv env, Object nonNull) {
            throw new UnsupportedOperationException();
        }


    }// _NullType


    /**
     * <p>
     * This class representing sql {@code DEFAULT} key word.
     * </p>
     *
     * @see SQLs#DEFAULT
     */
    private static final class DefaultWord extends NonOperationExpression
            implements WordDefault, ArmyKeyWord {

        private DefaultWord() {
        }

        @Override
        public String spaceRender() {
            return _Constant.SPACE_DEFAULT;
        }

        @Override
        public TypeMeta typeMeta() {
            throw unsupportedOperation(this);
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE_DEFAULT);
        }

        @Override
        public String toString() {
            return _Constant.SPACE_DEFAULT;
        }


    }// DefaultWord

    /**
     * @see SQLs#_ASTERISK_EXP
     */
    private static final class LiteralSymbolAsterisk extends NonOperationExpression
            implements FunctionArg.SingleFunctionArg {

        private LiteralSymbolAsterisk() {
        }

        @Override
        public TypeMeta typeMeta() {
            throw unsupportedOperation(this);
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(" *");
        }

        @Override
        public String toString() {
            return " *";
        }


    }//LiteralSymbolAsterisk


}
