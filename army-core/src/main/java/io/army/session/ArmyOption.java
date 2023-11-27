package io.army.session;

import io.army.session.record.ResultStates;
import io.army.util._Collections;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public final class ArmyOption<T> {


    @SuppressWarnings("unchecked")
    public static <T> ArmyOption<T> from(final String name, final Class<T> javaType) {
        if (!_StringUtils.hasText(name)) {
            throw new IllegalArgumentException("no text");
        }
        Objects.requireNonNull(javaType);

        final ArmyOption<?> option;
        option = INSTANCE_MAP.computeIfAbsent(name, key -> new ArmyOption<>(name, javaType));

        if (option.javaType == javaType) {
            return (ArmyOption<T>) option;
        }
        final String newName = name + "#" + javaType.getName();
        return (ArmyOption<T>) INSTANCE_MAP.computeIfAbsent(newName, cacheKey -> new ArmyOption<>(name, javaType)); // here , still use name not cacheKey
    }

    public static <T> Function<ArmyOption<?>, ?> singleFunc(final ArmyOption<T> option, final @Nullable T value) {
        return o -> {
            if (option.equals(o)) {
                return value;
            }
            return null;
        };
    }

    public static final Function<ArmyOption<?>, ?> EMPTY_OPTION_FUNC = option -> null;

    /**
     * private
     */
    private static final ConcurrentMap<String, ArmyOption<?>> INSTANCE_MAP = _Collections.concurrentHashMap();


    /**
     * <p>
     * Representing a name option. For example : transaction name in firebird database.
     * <br/>
     */
    public static final ArmyOption<String> NAME = ArmyOption.from("NAME", String.class);

    /**
     * <p>
     * Representing a name option. For example : transaction label that is print only by {@link TransactionInfo#toString()}
     * <br/>
     */
    public static final ArmyOption<String> LABEL = ArmyOption.from("LABEL", String.class);

    /**
     * <p>
     * Representing transaction TIMEOUT milliseconds ,for example transaction timeout.
     * <br/>
     */
    public static final ArmyOption<Integer> TIMEOUT = ArmyOption.from("TIMEOUT", Integer.class);

    public static final ArmyOption<Long> START_MILLIS = ArmyOption.from("START MILLIS", Long.class);

    /**
     * <p>
     * Representing a wait option. For example : transaction wait option.
     * <br/>
     *
     * @see <a href="https://firebirdsql.org/file/documentation/html/en/refdocs/fblangref40/firebird-40-language-reference.html#fblangref40-transacs-settransac">firebird : SET TRANSACTION</a>
     */
    public static final ArmyOption<Boolean> WAIT = ArmyOption.from("WAIT", Boolean.class);

    /**
     * <p>
     * Representing transaction LOCK TIMEOUT milliseconds ,for example firebird database.
     * <br/>
     *
     * @see <a href="https://firebirdsql.org/file/documentation/html/en/refdocs/fblangref40/firebird-40-language-reference.html#fblangref40-transacs-settransac">firebird : SET TRANSACTION</a>
     */
    public static final ArmyOption<Integer> LOCK_TIMEOUT = ArmyOption.from("LOCK TIMEOUT", Integer.class);


    /**
     * <p>
     * This option representing transaction isolation level.
     * <br/>
     * <p>
     * This option always is supported by {@link TransactionOption#valueOf(ArmyOption)}.
     * <br/>
     *
     * @see #READ_ONLY
     */
    public static final ArmyOption<Isolation> ISOLATION = ArmyOption.from("ISOLATION", Isolation.class);

    /**
     * <p>
     * This option representing read-only transaction.
     * <br/>
     * <p>
     * This option always is supported by {@link TransactionOption#valueOf(ArmyOption)}.
     * <br/>
     * <p>
     * When this option is supported by {@link Session#valueOf(ArmyOption)} , this option representing the session in read-only transaction block<br/>
     * after last statement executing , now the {@link #IN_TRANSACTION} always true.
     * <br/>
     * <p>
     * When this option is supported by {@link ResultStates#valueOf(ArmyOption)} , this option representing the session in read-only transaction block
     * after current statement executing, now the {@link #IN_TRANSACTION} always true. <br/>
     * <strong>NOTE</strong> : the 'current' statement perhaps is a part of multi-statement or is CALL command that execute procedures,<br/>
     * that means the read-only transaction maybe have ended by next statement.
     * <br/>
     *
     * @see #IN_TRANSACTION
     */
    public static final ArmyOption<Boolean> READ_ONLY = ArmyOption.from("READ ONLY", Boolean.class);


    /**
     * <p>
     * This option representing {@link Session} in transaction block.
     * <br/>
     * <p>
     * This option always is supported by {@link TransactionInfo#valueOf(ArmyOption)}.
     * <br/>
     * <p>
     * When this option is supported by {@link Session#valueOf(ArmyOption)} , this option representing the session in transaction block<br/>
     * after last statement executing. Now this option is equivalent to {@link Session#inTransaction()}.
     * <br/>
     * <p>
     * When this option is supported by {@link ResultStates#valueOf(ArmyOption)} , this option representing the session in transaction block
     * after current statement executing.<br/>
     * <strong>NOTE</strong> : the 'current' statement perhaps is a part of multi-statement or is CALL command that execute procedures<br/>
     * that means the transaction block maybe have ended by next statement.
     * <br/>
     *
     * @see #READ_ONLY
     * @see Session#inTransaction()
     */
    public static final ArmyOption<Boolean> IN_TRANSACTION = ArmyOption.from("IN TRANSACTION", Boolean.class);

    public static final ArmyOption<Boolean> ROLLBACK_ONLY = ArmyOption.from("ROLLBACK ONLY", Boolean.class);

    /**
     * <p>
     * When this option is supported by {@link Session#valueOf(ArmyOption)} , this option representing the session is auto commit<br/>
     * after last statement executing.
     * <br/>
     * <p>
     * When this option is supported by {@link ResultStates#valueOf(ArmyOption)} , this option representing the session is auto commit
     * after current statement executing.<br/>
     * <strong>NOTE</strong> : the 'current' statement perhaps is a part of multi-statement or is CALL command that execute procedures<br/>
     * that means the auto commit status maybe have modified by next statement.
     * <br/>
     *
     * @see #READ_ONLY
     * @see #IN_TRANSACTION
     */
    public static final ArmyOption<Boolean> AUTO_COMMIT = ArmyOption.from("AUTO COMMIT", Boolean.class);

    /**
     * <p>
     * representing the XID option of {@link TransactionInfo#valueOf(ArmyOption)} from {@link RmSession}.
     * <br/>
     *
     * @see Xid
     */
    public static final ArmyOption<Xid> XID = ArmyOption.from("XID", Xid.class);

    /**
     * <p>
     * representing the XA_STATES option of {@link TransactionInfo#valueOf(ArmyOption)} from {@link RmSession}.
     * <br/>
     *
     * @see XaStates
     */
    public static final ArmyOption<XaStates> XA_STATES = ArmyOption.from("XA STATES", XaStates.class);

    /**
     * <p>
     * representing the xa flags option of {@link TransactionInfo#valueOf(ArmyOption)} from {@link RmSession}.
     * <br/>
     */
    public static final ArmyOption<Integer> XA_FLAGS = ArmyOption.from("XA FLAGS", Integer.class);

    /**
     * <p>
     * This option representing {@link Session} is read only, <strong>usually</strong> (not always) database is read only. <br/>
     * That means application developer can't modify the read only status by sql.
     * <br/>
     * <p>
     * This option <strong>perhaps</strong> is supported by following :
     *     <ul>
     *         <li>{@link ResultStates#valueOf(ArmyOption)}</li>
     *     </ul>
     * <br/>
     */
    public static final ArmyOption<Boolean> READ_ONLY_SESSION = ArmyOption.from("READ ONLY SESSION", Boolean.class);

    /**
     * <p>
     * [NO] CHAIN option of COMMIT command.
     * <br/>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/commit.html">MySQL : COMMIT [WORK] [AND [NO] CHAIN]</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-commit.html">postgre : COMMIT [ WORK | TRANSACTION ] [ AND [ NO ] CHAIN ]</a>
     */
    public static final ArmyOption<Boolean> CHAIN = ArmyOption.from("CHAIN", Boolean.class);


    /**
     * <p>
     * [NO] RELEASE option of COMMIT/ROLLBACK command.
     * <br/>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/commit.html">MySQL : ROLLBACK [WORK] [[NO] RELEASE]</a>
     */
    public static final ArmyOption<Boolean> RELEASE = ArmyOption.from("RELEASE", Boolean.class);


    public static final ArmyOption<String> SQL_STATE = ArmyOption.from("SQL STATE", String.class);


    public static final ArmyOption<String> MESSAGE = ArmyOption.from("MESSAGE", String.class);


    public static final ArmyOption<Integer> VENDOR_CODE = ArmyOption.from("VENDOR CODE", Integer.class);


    public static final ArmyOption<Integer> WARNING_COUNT = ArmyOption.from("WARNING COUNT", Integer.class);


    public static final ArmyOption<String> USER = ArmyOption.from("USER", String.class);


    private final String name;

    private final Class<T> javaType;


    /**
     * private constructor
     */
    private ArmyOption(String name, Class<T> javaType) {
        this.name = name;
        this.javaType = javaType;
    }


    public String name() {
        return this.name;
    }

    public Class<T> javaType() {
        return this.javaType;
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.javaType);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof ArmyOption) {
            final ArmyOption<?> o = (ArmyOption<?>) obj;
            match = o.name.equals(this.name) && o.javaType == this.javaType;
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return String.format("%s[ name : %s , javaType : %s , hash : %s]",
                ArmyOption.class.getName(),
                this.name,
                this.javaType.getName(),
                System.identityHashCode(this)
        );
    }



    /*-------------------below private method -------------------*/

    private void readObject(ObjectInputStream in) throws IOException {
        throw new InvalidObjectException("can't deserialize Option");
    }

    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("can't deserialize Option");
    }


}
