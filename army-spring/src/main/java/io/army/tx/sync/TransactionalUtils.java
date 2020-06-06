package io.army.tx.sync;


import org.springframework.transaction.annotation.Isolation;

import java.util.EnumSet;
import java.util.Set;

/**
 * @see TransactionDefinitionHolder
 */
public abstract class TransactionalUtils {

    private static final Set<Isolation> GENERAL_ISOLATION = EnumSet.of(
            Isolation.READ_COMMITTED,
            Isolation.REPEATABLE_READ,
            Isolation.SERIALIZABLE
    );

    private static final Set<Isolation> AMOUNT_ISOLATION = EnumSet.of(
            Isolation.REPEATABLE_READ,
            Isolation.SERIALIZABLE
    );


    /**
     * @return true {@link Isolation#READ_COMMITTED}+.
     * @see TransactionDefinitionHolder#getIsolation()
     */
    public static boolean isGeneralIsolation() {
        return GENERAL_ISOLATION.contains(TransactionDefinitionHolder.getIsolation());
    }

    /**
     * @return true {@link Isolation#REPEATABLE_READ}+.
     * @see TransactionDefinitionHolder#getIsolation()
     */
    public static boolean isAmountIsolation() {
        return AMOUNT_ISOLATION.contains(TransactionDefinitionHolder.getIsolation());
    }

    /**
     * {@link Isolation#REPEATABLE_READ}+.
     *
     * @see #assertAmountIsolation(String)
     */
    public static void assertAmountIsolation() throws TransactionIsolationException {
        assertAmountIsolation("");
    }

    /**
     * assert {@link Isolation#REPEATABLE_READ}+.
     *
     * @see #AMOUNT_ISOLATION
     */
    public static void assertAmountIsolation(String message) throws TransactionIsolationException {
        if (!isAmountIsolation()) {
            throw new TransactionIsolationException(message);
        }
    }

    /**
     * assert  {@link Isolation#READ_COMMITTED}+.
     *
     * @see #assertGeneralIsolation(String)
     */
    public static void assertGeneralIsolation() throws TransactionIsolationException {
        assertGeneralIsolation("");
    }

    /**
     * assert  {@link Isolation#READ_COMMITTED}+.
     *
     * @see #GENERAL_ISOLATION
     */
    public static void assertGeneralIsolation(String message) throws TransactionIsolationException {
        if (!isGeneralIsolation()) {
            throw new TransactionIsolationException(message);
        }
    }


}
