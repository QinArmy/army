package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
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
import io.army.mapping._ArmyInnerMapping;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.modelgen._MetaBridge;
import io.army.sqltype.MySQLType;
import io.army.sqltype.SqlType;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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


    public static final WordAs AS = KeyWordAs.AS;

    public static final WordAnd AND = KeyWordAnd.AND;


    public static final TrimPosition BOTH = SqlWords.WordTrimPosition.BOTH;
    public static final TrimPosition LEADING = SqlWords.WordTrimPosition.LEADING;
    public static final TrimPosition TRAILING = SqlWords.WordTrimPosition.TRAILING;
    public static final WordIn IN = SqlWords.KeyWordIn.IN;
    public static final WordFor FOR = SqlWords.KeyWordFor.FOR;
    public static final WordFrom FROM = SqlWords.KeyWordFrom.FROM;
    public static final WordSimilar SIMILAR = SqlWords.KeyWordSimilar.SIMILAR;

    @Support({PostgreSQL, H2})
    public static final BetweenModifier SYMMETRIC = KeyWordSymmetric.SYMMETRIC;

    @Support({H2})
    public static final BetweenModifier ASYMMETRIC = KeyWordSymmetric.ASYMMETRIC;

    public static final SymbolAsterisk ASTERISK = SQLSymbolAsterisk.ASTERISK;

    public static final SymbolPeriod PERIOD = SQLSymbolPeriod.PERIOD;

    public static final WordBooleans TRUE = OperationPredicate.booleanWord(true);

    public static final WordBooleans FALSE = OperationPredicate.booleanWord(false);

    public static final WordDefault DEFAULT = new DefaultWord();

    public static final WordNull NULL = OperationExpression.nullWord();

    public static final WordEscape ESCAPE = KeyWordEscape.ESCAPE;
    /**
     * package field
     */
    static final Expression _ASTERISK_EXP = new LiteralSymbolAsterisk();

    private static final Function<? extends Item, ? extends Item> _IDENTITY = SQLs::_identity;

    static final Function<InsertStatement, InsertStatement> _INSERT_IDENTITY = _getIdentity();

    static final Function<Select, Select> _SELECT_IDENTITY = _getIdentity();

    static final Function<Update, Update> _UPDATE_IDENTITY = _getIdentity();
    static final Function<Delete, Delete> _DELETE_IDENTITY = _getIdentity();

    static final Function<SubQuery, SubQuery> _SUB_QUERY_IDENTITY = _getIdentity();

    static final Function<SubQuery, Expression> _SCALAR_QUERY_IDENTITY = Expressions::scalarExpression;


    public static StandardInsert._PrimaryOptionSpec singleInsert() {
        return StandardInserts.singleInsert();
    }

    public static StandardUpdate._DomainUpdateClause domainUpdate() {
        return StandardUpdates.simpleDomain();
    }


    public static StandardUpdate._SingleUpdateClause<Update> singleUpdate() {
        return StandardUpdates.singleUpdate();
    }


    /**
     * <p>
     * Batch domain update
     * </p>
     */
    public static StandardUpdate._BatchDomainUpdateClause batchDomainUpdate() {
        return StandardUpdates.batchDomain();
    }


    /**
     * <p>
     * Batch domain update
     * </p>
     */
    public static StandardUpdate._BatchSingleUpdateClause batchSingleUpdate() {
        return StandardUpdates.batchSingle();
    }


    public static StandardDelete._StandardDeleteClause<Delete> singleDelete() {
        return StandardDeletes.singleDelete();
    }

    public static StandardDelete._DomainDeleteClause domainDelete() {
        return StandardDeletes.domainDelete();
    }

    /**
     * <p>
     * Batch domain delete
     * </p>
     */
    public static StandardDelete._BatchDeleteClause batchSingleDelete() {
        return StandardDeletes.batchSingleDelete();
    }

    public static StandardDelete._BatchDomainDeleteClause batchDomainDelete() {
        return StandardDeletes.batchDomainDelete();
    }


    public static StandardQuery._SelectSpec<Select> query() {
        return StandardQueries.simpleQuery();
    }

    public static StandardQuery._SelectSpec<SubQuery> subQuery() {
        return StandardQueries.subQuery(ContextStack.peek(), SQLs::_identity);
    }


    public static StandardQuery._SelectSpec<Expression> scalarSubQuery() {
        return StandardQueries.subQuery(ContextStack.peek(), Expressions::scalarExpression);
    }


    /**
     * <p>
     * package method that is used by army developer.
     * </p>
     *
     * @param value {@link Expression} or parameter.
     * @see #plusEqual(DataField, Expression)
     */
    static SQLs.ArmyItemPair _itemPair(final @Nullable DataField field, final @Nullable AssignOperator operator,
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
    static _ItemPair _itemExpPair(final DataField field, @Nullable Expression value) {
        assert value != null;
        return SQLs._itemPair(field, null, value);
    }

    static ItemPair _itemPair(List<? extends DataField> fieldList, SubQuery subQuery) {
        return new SQLs.RowItemPair(fieldList, subQuery);
    }


    /**
     * <p>
     * This method is similar to {@link Function#identity()}, except that use method reference.
     * </p>
     *
     * @see Function#identity()
     */
    static <T extends Item> T _identity(T t) {
        return t;
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

    @SuppressWarnings("unchecked")
    static <T extends Item> Function<T, T> _getIdentity() {
        return (Function<T, T>) _IDENTITY;
    }





    /*-------------------below package method-------------------*/


    static abstract class ArmyItemPair implements _ItemPair {

        final RightOperand right;

        private ArmyItemPair(RightOperand right) {
            this.right = right;
        }
    }//ArmyItemPair

    /**
     * @see #_itemPair(DataField, AssignOperator, Expression)
     */
    static class FieldItemPair extends ArmyItemPair implements _ItemPair._FieldItemPair {

        final DataField field;

        private FieldItemPair(DataField field, ArmyExpression value) {
            super(value);
            this.field = field;
        }

        @Override
        public final void appendItemPair(final _SetClauseContext context) {
            final DataField field = this.field;
            //1. append left item
            context.appendSetLeftItem(field);
            //2. append operator
            if (this instanceof OperatorItemPair) {
                ((OperatorItemPair) this).operator
                        .appendOperator(context.parser().dialect(), field, context);
            } else {
                context.sqlBuilder()
                        .append(_Constant.SPACE_EQUAL);
            }
            //3. append right item
            ((_Expression) this.right).appendSql(context);
        }

        @Override
        public final DataField field() {
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

        private OperatorItemPair(DataField field, AssignOperator operator, ArmyExpression value) {
            super(field, value);
            this.operator = operator;
        }


    }//OperatorItemPair

    static final class RowItemPair extends ArmyItemPair implements _ItemPair._RowItemPair {

        final List<DataField> fieldList;

        private RowItemPair(List<? extends DataField> fieldList, SubQuery subQuery) {
            super(subQuery);
            final int selectionCount;
            selectionCount = ((_RowSet) subQuery).selectionSize();
            if (fieldList.size() != selectionCount) {
                String m = String.format("Row column count[%s] and selection count[%s] of SubQuery not match."
                        , fieldList.size(), selectionCount);
                throw new CriteriaException(m);
            }
            final List<DataField> tempList = new ArrayList<>(fieldList.size());
            for (DataField field : fieldList) {
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
        public void appendItemPair(final _SetClauseContext context) {
            final List<? extends DataField> fieldList = this.fieldList;
            final int fieldSize = fieldList.size();
            //1. append left paren
            final StringBuilder sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_LEFT_PAREN);
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
            context.parser().subQuery((SubQuery) this.right, context);

        }

        @Override
        public List<? extends DataField> rowFieldList() {
            return this.fieldList;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            //1. append left paren
            builder.append(_Constant.SPACE_LEFT_PAREN);
            final List<? extends DataField> fieldList = this.fieldList;
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
                this.selectionMap = CriteriaUtils.createAliasSelectionMap(this.columnNameList, (_DerivedTable) subStatement);
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


    private enum KeyWordAs implements WordAs, ArmyKeyWord {

        AS(" AS");

        private final String spaceWord;

        KeyWordAs(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordAs

    private enum KeyWordAnd implements WordAnd, ArmyKeyWord {

        AND(" AND");

        private final String spaceWord;

        KeyWordAnd(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordAnd


    private enum KeyWordSymmetric implements BetweenModifier, ArmyKeyWord {

        SYMMETRIC(" SYMMETRIC"),
        ASYMMETRIC(" ASYMMETRIC");

        private final String spaceWord;

        KeyWordSymmetric(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordSymmetric

    private enum SQLSymbolPeriod implements SymbolPeriod {

        PERIOD;

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//SQLSymbolPoint

    private enum SQLSymbolAsterisk implements SymbolAsterisk, SQLWords {

        ASTERISK(" *");

        private final String spaceStar;

        SQLSymbolAsterisk(String spaceStar) {
            this.spaceStar = spaceStar;
        }

        @Override
        public final String spaceRender() {
            return this.spaceStar;
        }

        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//SQLSymbolStar

    private enum KeyWordEscape implements WordEscape, ArmyKeyWord {

        ESCAPE(" ESCAPE");

        private final String spaceWord;

        KeyWordEscape(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWord;
        }


        @Override
        public final String toString() {
            return sqlKeyWordsToString(this);
        }

    }//KeyWordEscape


    @Deprecated
    static final class _NullType extends _ArmyInnerMapping {

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

        private static final DefaultWord INSTANCE = new DefaultWord();

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
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder().append(_Constant.SPACE_DEFAULT);
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

        private static final LiteralSymbolAsterisk INSTANCE = new LiteralSymbolAsterisk();

        private LiteralSymbolAsterisk() {
        }

        @Override
        public TypeMeta typeMeta() {
            throw unsupportedOperation(this);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder().append(" *");
        }

        @Override
        public String toString() {
            return " *";
        }


    }//LiteralSymbolAsterisk


}
