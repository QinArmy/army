package io.army.context.spi;

import io.army.ArmyRuntimeException;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public interface CurrentSessionContext {

    /**
     * Retrieve the current session according to the scoping defined
     * by this implementation.
     *
     * @return The current session.
     * @throws ArmyRuntimeException Typically indicates an issue
     * locating or creating the current session.
     */
     Session currentSession() throws ArmyRuntimeException;
}
