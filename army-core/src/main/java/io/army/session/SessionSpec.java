package io.army.session;

import io.army.criteria.Visible;

/**
 * <p>This interface is base interface of {@link Session} for pass to {@link io.army.dialect.DialectParser}.
 */
public interface SessionSpec extends OptionSpec {


    /**
     * <p>Get the name of session.
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    String name();


    /**
     * <p>Get the visible mode(soft delete) of session.
     * <p><strong>NOTE</strong> : This method don't check whether session closed or not.
     */
    Visible visible();

}
